/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.DataFormatException;
import lev.LInChannel;
import lev.LFlags;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import io.airlift.compress.lz4.Lz4Decompressor;
import java.nio.ByteBuffer;

/**
 * An object that interfaces with BSA files, allowing for queries of its
 * contents and file data extraction.
 *
 * @author Justin Swanson
 */
public class BSA {
    static Map<String, BSA> bsaLookup = new TreeMap<>();
    static ArrayList<BSA> resourceLoadOrder;
    static Map<ModListing, BSA> pluginLoadOrder = new TreeMap<>();
    static boolean pluginsLoaded = false;
    static boolean overlapDeleted = false;
    static String header = "BSA";
    String filePath;
    int offset;
    LFlags archiveFlags;
    int folderCount;
    int fileCount;
    int folderNameLength;
    int fileNameLength;
    LFlags fileFlags;
    boolean loaded = false;
    boolean bad = false;
    Map<String, BSAFolder> folders;
    LInChannel in = new LInChannel();

    BSA(File file, boolean load) throws FileNotFoundException, IOException, BadParameter {
        this(file.getPath(), load);
    }

    BSA(String filePath, boolean load) throws FileNotFoundException, IOException, BadParameter {
        this.filePath = filePath;
        in.openFile(filePath);
        //if (!in.extractString(0, 3).equals("BSA") || in.extractInt(1, 4) != 104) {
        if (!in.extractString(0, 3).equals("BSA") || in.extractInt(1, 4) != 105) {
            throw new BadParameter("Was not a BSA file of version 104: " + filePath);
        }
        offset = in.extractInt(0, 4);
        archiveFlags = new LFlags(in.extract(0, 4));
        folderCount = in.extractInt(0, 4);
        folders = new HashMap<>(folderCount);
        fileCount = in.extractInt(0, 4);
        folderNameLength = in.extractInt(0, 4);
        fileNameLength = in.extractInt(0, 4);
        fileFlags = new LFlags(in.extract(0, 4));
        if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
            SPGlobal.logSpecial(LogTypes.BSA, header, "|==================>");
            SPGlobal.logSpecial(LogTypes.BSA, header, "| Imported " + filePath);
            SPGlobal.logSpecial(LogTypes.BSA, header, "| Offset " + offset + ", archiveFlags: " + archiveFlags);
            SPGlobal.logSpecial(LogTypes.BSA, header, "| hasDirectoryNames: " + archiveFlags.get(0) + ", hasFileNames: " + archiveFlags.get(1) + ", compressed: " + archiveFlags.get(2));
            SPGlobal.logSpecial(LogTypes.BSA, header, "| FolderCount: " + Ln.prettyPrintHex(folderCount) + ", FileCount: " + Ln.prettyPrintHex(fileCount));
            SPGlobal.logSpecial(LogTypes.BSA, header, "| totalFolderNameLength: " + Ln.prettyPrintHex(folderNameLength) + ", totalFileNameLength: " + Ln.prettyPrintHex(fileNameLength) + ", NamesInFileData: " + is(BSAFlag.NamesInFileData));
            SPGlobal.logSpecial(LogTypes.BSA, header, "| fileFlags: " + fileFlags.toString());
            SPGlobal.logSpecial(LogTypes.BSA, header, "|==================>");
        }
        if (load) {
            loadFolders();
        }
    }

    /**
     *
     * @param filePath Filepath to load BSA data from.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws BadParameter If the BSA is malformed (by SkyProc standards)
     */
    public BSA(String filePath) throws FileNotFoundException, IOException, BadParameter {
        this(filePath, true);
    }

    final void loadFolders() {
        if (loaded) {
            return;
        }
        loaded = true;
        if (SPGlobal.logging()) {
            SPGlobal.logSpecial(LogTypes.BSA, header, "|============================================");
            SPGlobal.logSpecial(LogTypes.BSA, header, "|============  Loading " + this + " ============");
            SPGlobal.logSpecial(LogTypes.BSA, header, "|============================================");
        }
        try {
            String fileName;
            int fileCounter = 0;
            in.pos(offset);
            ArrayList<BSAFolder> temp_folders = new ArrayList<>();
            int fileRecordsSize = folderCount + folderNameLength + fileCount * 16;
            for (int i = 0; i < folderCount; i++) {
                BSAFolder folder = new BSAFolder();
                in.skip(8); // Skip Hash
                folder.setFileCount(in.extractInt(4));
                in.skip(4);
                folder.dataPos = in.extractLong(0, 8);
                temp_folders.add(folder);
            }
            LShrinkArray fileRecords  = new LShrinkArray(in.extract(0, fileRecordsSize));
            LShrinkArray fileNames = new LShrinkArray(in.extract(0, fileNameLength));

            int fileNameListPos = 0;
            int startOfFileRecords = 36 + 24 * folderCount;
            for(int i = 0; i < folderCount; i++)
            {
                BSAFolder folder = temp_folders.get(i);
                folder.dataPos -= fileNameLength + startOfFileRecords;
                fileRecords.pos(folder.dataPos);
                int folderNameLength_ = fileRecords.read() - 1;
                fileRecords.pos(folder.dataPos + 1);
                folder.name = fileRecords.extractString(0, folderNameLength_);
                folder.name = folder.name.toUpperCase();
                if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
                    SPGlobal.logSpecial(LogTypes.BSA, header, "Loaded folder: " + folder.name);
                }
                long startOfFolderFileRecords  = folder.dataPos + folderNameLength_ + 2;
                for(int j = 0; j < folder.fileCount; j++)
                {
                    BSAFileRef f = new BSAFileRef();
                    fileRecords.pos(startOfFolderFileRecords + j * 16);
                    fileRecords.skip(8); // Skip Hash
                    f.size = fileRecords.extractInt(3); 
                    LFlags sizeFlag = new LFlags(fileRecords.extract(1));
                    f.flippedCompression = sizeFlag.get(6);
                    f.dataOffset = fileRecords.extractInt(4);
                    fileNames.pos(fileNameListPos);
                    fileName = "";
                    while(true)
                    {
                        int r = fileNames.read();
                        if (r == 0)
                        {
                                break;
                        }
                        fileNameListPos++;
                        fileName += (char)r;
                    }
                    fileNameListPos++;
                    folder.files.put(fileName.toUpperCase(), f);
                    if (SPGlobal.logging()) {
                        SPGlobal.logSpecial(LogTypes.BSA, header, "  " + fileName + ", size: " + Ln.prettyPrintHex(f.size) + ", offset: " + Ln.prettyPrintHex(f.dataOffset) + ", flipped: " + f.flippedCompression);
                        fileCounter++;
                    }
                }
                folders.put(folder.name, folder);
            }
            if (SPGlobal.logging()) {
                if (SPGlobal.debugBSAimport) {
                    SPGlobal.logSpecial(LogTypes.BSA, header, "Loaded " + fileCounter + " files.");
                }
                SPGlobal.logSpecial(LogTypes.BSA, header, "Loaded BSA: " + getFilePath());
            }
        } catch (Exception e) {
            SPGlobal.logException(e);
            SPGlobal.logError("BSA", "Skipped BSA " + this);
            bad = true;
        }
    }

    void posAtFilenames() {
        in.pos(folderNameLength + fileCount * 16 + folderCount * 17 + offset);
    }

    void posAtFolder(BSAFolder folder) {
        in.pos(folder.dataPos - fileNameLength);
    }

    /**
     *
     * @return True if BSA has loaded it's folder listings.
     */
    public boolean loaded() {
        return loaded;
    }

    /**
     *
     * @param filePath filepath to query for and retrieve.
     * @return ShrinkArray of the raw data from the BSA of the file specified,
     * already decompressed if applicable; Empty ShrinkArray if the file did not
     * exist.
     * @throws IOException
     * @throws DataFormatException
     */
    public LShrinkArray getFile(String filePath1) throws IOException, DataFormatException {
        BSAFileRef ref;
        if ((ref = getFileRef(filePath1)) != null) {
            in.pos(ref.dataOffset);
            int aSize = ref.size;
            if (is(BSAFlag.NamesInFileData))
            {
                while(true)
                {
                    int r = in.read();
                    if (r == 0)
                    {
                        break;
                    }
                    aSize--;
                }
                aSize--;
            }
            if (isCompressed(ref))
            {
                int uncompressedSize = Ln.arrayToInt(in.extractInts(4));
                aSize -= 4;
                byte[] compressedByteData = in.extract(aSize);

                Lz4Decompressor decompressor2 = new Lz4Decompressor();
                byte[] uncompressedByteData = new byte[uncompressedSize];

                int decompressedLength2 = decompressor2.decompress(compressedByteData, 0, aSize, uncompressedByteData, 0, uncompressedSize);
                return new LShrinkArray(ByteBuffer.wrap(uncompressedByteData));
            }
            return new LShrinkArray(in.extract(0, aSize));
        }
        return new LShrinkArray(new byte[0]);
    }

    void trimName(LShrinkArray out) {
        if (is(BSAFlag.NamesInFileData)) {
            out.skip(out.extractInt(1));
        }
    }

    long getFileLocation(BSAFileRef ref) {
        return ref.dataOffset;
    }

    /**
     *
     * @param filePath
     * @return
     */
    long getFileLocation(String filePath) {
        BSAFileRef ref;
        if ((ref = getFileRef(filePath)) != null) {
            return getFileLocation(ref);
        }
        return -1;
    }

    /**
     *
     * @param f
     * @return
     */
    long getFileLocation(File f) {
        return getFileLocation(f.getPath());
    }

    /**
     * Returns a ShrinkArray containing the data of the file desired. <br>
     * Returns loose files if they exist, or the dominant BSA if they do not.
     *
     * @param f
     * @return
     * @throws IOException
     * @throws DataFormatException
     */
    public LShrinkArray getFile(File f) throws IOException, DataFormatException {
        return getFile(f.getPath());
    }

    String getFilename(String filePath) throws IOException {
        BSAFileRef ref;
        if ((ref = getFileRef(filePath)) != null) {
            in.pos(ref.nameOffset);
            return in.extractString();
        }
        return "";
    }

    static String getUsedFilename(String filePath) throws IOException {
        String tmp, out = "";
        File file = new File(filePath);
        if (!(file = Ln.getFilepathCaseInsensitive(file)).getPath().equals("")) {
            return file.getName();
        }
        Iterator<BSA> bsas = BSA.iterator();
        while (bsas.hasNext()) {
            tmp = bsas.next().getFilename(filePath);
            if (!tmp.equals("")) {
                out = tmp;
            }
        }
        return out;
    }

    /**
     *
     * @param filePath File to query for.
     * @return The used file, which prioritizes loose files first, and then
     * BSAs.<br> NOTE: Not fully sophisticated yet for prioritizing between
     * BSAs.
     * @throws IOException
     * @throws DataFormatException
     */
    static public LShrinkArray getUsedFile(String filePath) throws IOException, DataFormatException {
        File outsideBSA = new File(SPGlobal.pathToDataFixed + filePath);
        if (outsideBSA.isFile()) {
            SPGlobal.logSpecial(LogTypes.BSA, header, "Loaded from loose files: " + outsideBSA.getPath());
            return new LShrinkArray(outsideBSA);
        } else {
            Iterator<BSA> bsas = BSA.iterator();
            BSA tmp, bsa = null;
            while (bsas.hasNext()) {
                tmp = bsas.next();
                if (tmp.hasFile(filePath)) {
                    bsa = tmp;
                }
            }
            if (bsa != null) {
                if (SPGlobal.logging()) {
                    SPGlobal.logSpecial(LogTypes.BSA, header, "Loaded from BSA " + bsa.getFilePath() + ": " + filePath);
                }
                return bsa.getFile(filePath);
            }
        }
        return null;
    }

    static void loadPluginLoadOrder() {
        if (pluginsLoaded) {
            return;
        }
        if (SPGlobal.logging()) {
            SPGlobal.logSpecial(LogTypes.BSA, header, "Loading in active plugin BSA headers.");
        }
        try {
            ArrayList<ModListing> activeMods = SPImporter.getActiveModList();
            for (ModListing m : activeMods) {
                if (!pluginLoadOrder.containsKey(m)) {
                    BSA bsa = getBSA(m);
                    if (bsa != null) {
                        pluginLoadOrder.put(m, bsa);
                    }
                }
            }
        } catch (IOException ex) {
            SPGlobal.logException(ex);
        }

        pluginsLoaded = true;
    }

    static void loadResourceLoadOrder() {
        if (resourceLoadOrder != null) {
            return;
        }
        try {
            ArrayList<String> resources = new ArrayList<>();
            boolean line1 = false, line2 = false;
            try {
                File ini = SPGlobal.getSkyrimINI();

                if (SPGlobal.logging()) {
                    SPGlobal.logSpecial(LogTypes.BSA, header, "Loading in BSA list from Skyrim.ini: " + ini);
                }
                LInChannel input = new LInChannel(ini);

                String line = "";
                // First line
                while (input.available() > 0 && !line.toUpperCase().contains("SRESOURCEARCHIVELIST")) {
                    line = input.extractLine();
                }
                if (line.toUpperCase().contains("SRESOURCEARCHIVELIST2")) {
                    line2 = true;
                    resources.addAll(processINIline(line));
                } else {
                    line1 = true;
                    resources.addAll(0, processINIline(line));
                }

                // Second line
                line = "";
                while (input.available() > 0 && !line.toUpperCase().contains("SRESOURCEARCHIVELIST")) {
                    line = Ln.cleanLine(input.extractLine(), "#");
                }
                if (line.toUpperCase().contains("SRESOURCEARCHIVELIST2")) {
                    line2 = true;
                    resources.addAll(processINIline(line));
                } else {
                    line1 = true;
                    resources.addAll(0, processINIline(line));
                }
            } catch (IOException e) {
                SPGlobal.logException(e);
            }

            if (!line1 || !line2) {
                //Assume standard BSA listing
                if (!resources.contains("Skyrim - Misc.bsa")) {
                    resources.add("Skyrim - Misc.bsa");
                }

                if (!resources.contains("Skyrim - Shaders.bsa")) {
                    resources.add("Skyrim - Shaders.bsa");
                }

                if (!resources.contains("Skyrim - Textures0.bsa")) {
                    resources.add("Skyrim - Textures0.bsa");
                }
                if (!resources.contains("Skyrim - Textures1.bsa")) {
                    resources.add("Skyrim - Textures1.bsa");
                }
                if (!resources.contains("Skyrim - Textures2.bsa")) {
                    resources.add("Skyrim - Textures2.bsa");
                }
                if (!resources.contains("Skyrim - Textures3.bsa")) {
                    resources.add("Skyrim - Textures3.bsa");
                }
                if (!resources.contains("Skyrim - Textures4.bsa")) {
                    resources.add("Skyrim - Textures4.bsa");
                }
                if (!resources.contains("Skyrim - Textures5.bsa")) {
                    resources.add("Skyrim - Textures5.bsa");
                }
                if (!resources.contains("Skyrim - Textures6.bsa")) {
                    resources.add("Skyrim - Textures6.bsa");
                }
                if (!resources.contains("Skyrim - Textures7.bsa")) {
                    resources.add("Skyrim - Textures7.bsa");
                }
                if (!resources.contains("Skyrim - Textures8.bsa")) {
                    resources.add("Skyrim - Textures8.bsa");
                }

                if (!resources.contains("Skyrim - Interface.bsa")) {
                    resources.add("Skyrim - Interface9.bsa");
                }

                if (!resources.contains("Skyrim - Animations.bsa")) {
                    resources.add("Skyrim - Animations.bsa");
                }

                if (!resources.contains("Skyrim - Meshes0.bsa")) {
                    resources.add("Skyrim - Meshes0.bsa");
                }

                if (!resources.contains("Skyrim - Meshes1.bsa")) {
                    resources.add("Skyrim - Meshes1.bsa");
                }

                if (!resources.contains("Skyrim - Sounds.bsa")) {
                    resources.add("Skyrim - Sounds.bsa");
                }

                if (!resources.contains("Skyrim - Voices_en0.bsa")) {
                    resources.add("Skyrim - Voices_en0.bsa");
                }

                if (!resources.contains("Skyrim - Patch.bsa")) {
                    resources.add("Skyrim - Patch.bsa");
                }
            }

            if (SPGlobal.logging()) {
                SPGlobal.logSpecial(LogTypes.BSA, header, "BSA resource load order: ");
                for (String s : resources) {
                    SPGlobal.logSpecial(LogTypes.BSA, header, "  " + s);
                }
                SPGlobal.logSpecial(LogTypes.BSA, header, "Loading in their headers.");
            }

            // Get BSAs loaded from all active pluging's plugin.ini files
            ArrayList<ModListing> activeMods = SPImporter.getActiveModList();
            for (ModListing m : activeMods) {
                File pluginIni = new File(SPGlobal.pathToDataFixed + Ln.changeFileTypeTo(m.print(), "ini"));
                if (pluginIni.exists()) {
                    LInChannel input = new LInChannel(pluginIni);

                    String line = "";
                    // First line
                    while (input.available() > 0 && !line.toUpperCase().contains("SRESOURCEARCHIVELIST")) {
                        line = input.extractLine();
                    }
                    if (line.toUpperCase().contains("SRESOURCEARCHIVELIST2")) {
                        resources.addAll(processINIline(line));
                    } else {
                        resources.addAll(0, processINIline(line));
                    }

                    // Second line
                    line = "";
                    while (input.available() > 0 && !line.toUpperCase().contains("SRESOURCEARCHIVELIST")) {
                        line = Ln.cleanLine(input.extractLine(), "#");
                    }
                    if (line.toUpperCase().contains("SRESOURCEARCHIVELIST2")) {
                        resources.addAll(processINIline(line));
                    } else {
                        resources.addAll(0, processINIline(line));
                    }
                }
            }

            resourceLoadOrder = new ArrayList<>(resources.size());
            for (String s : resources) {
                File bsaPath = new File(SPGlobal.pathToDataFixed + s);
                if (bsaPath.exists()) {
                    try {
                        if (SPGlobal.logging()) {
                            SPGlobal.logSpecial(LogTypes.BSA, header, "Loading: " + bsaPath);
                        }
                        BSA bsa;
                        if (!bsaLookup.containsKey(bsaPath.getPath().toUpperCase()))
                        {
                            bsa = new BSA(bsaPath, false);
                            bsaLookup.put(bsaPath.getPath().toUpperCase(), bsa);
                        }
                        else
                        {
                            bsa = bsaLookup.get(bsaPath.getPath().toUpperCase());
                        }
                        resourceLoadOrder.add(bsa);
                    } catch (BadParameter | FileNotFoundException ex) {
                        logBSAError(s, ex);
                    }
                } else if (SPGlobal.logging()) {
                    SPGlobal.logSpecial(LogTypes.BSA, header, "  BSA skipped because it didn't exist: " + bsaPath);
                }
            }

        } catch (IOException ex) {
            SPGlobal.logException(ex);
        }
    }

    static ArrayList<String> processINIline(String in) {
        if (SPGlobal.logging()) {
            SPGlobal.logSpecial(LogTypes.BSA, header, "Processing line: " + in);
        }
        ArrayList<String> out = new ArrayList<>();
        int index = in.indexOf("=");
        if (index != -1) {
            in = in.substring(index + 1);
            String[] split = in.split(",");
            for (String s : split) {
                s = s.trim();
                if (!s.isEmpty()) {
                    out.add(s);
                }
            }
        }
        return out;
    }

    BSAFileRef getFileRef(String filePath) {
        filePath = filePath.toUpperCase();
        int index = filePath.lastIndexOf('\\');
        String folderPath = filePath.substring(0, index + 0);
        BSAFolder folder = folders.get(folderPath);
        if (folder != null) {
            String file = filePath.substring(index + 1);
            BSAFileRef ref = folder.files.get(file);
            if (ref != null) {
                return ref;
            }
        }
        return null;
    }

    /**
     *
     * @param filePath Filepath the query for.
     * @return True if BSA has a file with that path.
     */
    public boolean hasFile(String filePath) {
        return getFileRef(filePath) != null;
    }

    /**
     *
     * @param f
     * @return
     */
    public boolean hasFile(File f) {
        return hasFile(f.getPath());
    }

    /**
     *
     * @return The BSA's filepath.
     */
    public String getFilePath() {
        return filePath.substring(0, filePath.length());
    }

    /**
     *
     * @param folderPath Folder path to query for.
     * @return True if BSA has a folder with that path.
     */
    public boolean hasFolder(String folderPath) {
        filePath = filePath.toUpperCase();
        return folders.containsKey(folderPath);
    }

    /**
     *
     * @return A list of contained folders.
     */
    public Set<String> getFolders() {
        return folders.keySet();
    }

    /**
     *
     * @return Map containing folder paths as keys, and list of file paths as
     * values.
     */
    public Map<String, ArrayList<String>> getFiles() {
        Map<String, ArrayList<String>> out = new HashMap<>(folders.size());
        for (BSAFolder folder : folders.values()) {
            ArrayList<String> list = new ArrayList<>(folder.files.values().size());
            out.put(folder.name, list);
            list.addAll(folder.files.keySet());
        }
        return out;
    }

    /**
     *
     * @return Number of folders contained in the BSA
     */
    public int numFolders() {
        return folders.size();
    }

    /**
     *
     * @return Number of files contained in the BSA
     */
    public int numFiles() {
        int out = 0;
        for (BSAFolder folder : folders.values()) {
            out += folder.fileCount;
        }
        return out;
    }

    /**
     *
     * @param fileType Filetype to query for.
     * @return True if BSA contains files of that type.
     */
    public boolean contains(FileType fileType) {
        if (!fileFlags.isZeros()) {
            return fileFlags.get(fileType.ordinal());
        } else {
            return manualContains(fileType);
        }
    }

    boolean manualContains(FileType fileType) {
        FileType[] types = new FileType[1];
        types[0] = fileType;
        return manualContains(types);
    }

    boolean manualContains(FileType[] fileTypes) {
        loadFolders();
        for (BSAFolder folder : folders.values()) {
            for (String file : folder.files.keySet()) {
                for (FileType type : fileTypes) {
                    if (file.endsWith(type.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param fileTypes Filetypes to query for.
     * @return True if BSA contains any of the filetypes.
     */
    public boolean containsAny(FileType[] fileTypes) {
        if (!fileFlags.isZeros()) {
            for (FileType f : fileTypes) {
                if (contains(f)) {
                    return true;
                }
            }
            return false;
        } else {
            return manualContains(fileTypes);
        }
    }

    /**
     *
     * @param types Types to load in.
     * @return List of all BSA files that contain any of the filetypes.
     */
    public static ArrayList<BSA> loadInBSAs(FileType... types) {
        ArrayList<BSA> out = new ArrayList<>();
        Iterator<BSA> bsas = iterator();
        while (bsas.hasNext()) {
            BSA tmp = bsas.next();
            try {
                if (!tmp.bad && tmp.containsAny(types)) {
                    tmp.loadFolders();
                    out.add(tmp);
                }
            } catch (Exception e) {
                SPGlobal.logException(e);
                SPGlobal.logError("BSA", "Skipped BSA " + tmp);
            }
        }
        return out;
    }

    static void deleteOverlap() {
        if (!overlapDeleted) {
            return;
        }
        for (BSA b : pluginLoadOrder.values()) {
            resourceLoadOrder.remove(b);
        }
        overlapDeleted = true;

    }

    static Iterator<BSA> iterator() {
        return getBSAs().iterator();
    }

    static ArrayList<BSA> getBSAs() {
        loadResourceLoadOrder();
        loadPluginLoadOrder();
        deleteOverlap();

        ArrayList<BSA> order = new ArrayList<>(resourceLoadOrder.size() + pluginLoadOrder.size());
        order.addAll(resourceLoadOrder);
        order.addAll(pluginLoadOrder.values());
        return order;
    }

    static ArrayList<BSA> getResourceBSAa() {
        loadResourceLoadOrder();
        ArrayList<BSA> resources = new ArrayList<>(resourceLoadOrder.size());
        resources.addAll(resourceLoadOrder);
        return resources;
    }

    static ArrayList<BSA> getPluginBSAs() {
        loadPluginLoadOrder();
        ArrayList<BSA> resources = new ArrayList<>(pluginLoadOrder.size());
        resources.addAll(pluginLoadOrder.values());
        return resources;
    }

    /**
     * Returns BSA object associated with modlisting, or null if there is none.
     *
     * @param m
     * @return
     */
    static public BSA getBSA(ModListing m) {
        if (pluginLoadOrder.containsKey(m)) {
            return pluginLoadOrder.get(m);
        }

        File bsaPath = new File(SPGlobal.pathToDataFixed + Ln.changeFileTypeTo(m.print(), "bsa"));
        if (bsaPath.exists()) {
            try {
                BSA bsa;
                if (!bsaLookup.containsKey(bsaPath.getPath().toUpperCase()))
                {
                    bsa = new BSA(bsaPath, false);
                    bsaLookup.put(bsaPath.getPath().toUpperCase(), bsa);
                }
                else
                {
                    bsa = bsaLookup.get(bsaPath.getPath().toUpperCase());
                }
                pluginLoadOrder.put(m, bsa);
                return bsa;
            } catch (IOException | BadParameter ex) {
                logBSAError(m.printNoSuffix() + ".bsa", ex);
                return null;
            }
        }

        if (SPGlobal.logging()) {
            SPGlobal.logSpecial(LogTypes.BSA, header, "  BSA skipped because it didn't exist: " + bsaPath);
        }
        return null;
    }

    /**
     * Returns BSA object associated with mod, or null if there is none.
     *
     * @param m
     * @return
     */
    static public BSA getBSA(Mod m) {
        return getBSA(m.getInfo());
    }

    /**
     *
     * @param m
     * @return
     */
    static public boolean hasBSA(ModListing m) {
        File bsaPath = new File(SPGlobal.pathToDataFixed + Ln.changeFileTypeTo(m.print(), "bsa"));
        return bsaPath.exists();
    }

    /**
     *
     * @param m
     * @return
     */
    static public boolean hasBSA(Mod m) {
        return hasBSA(m.getInfo());
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BSA other = (BSA) obj;
        return Objects.equals(this.filePath, other.filePath);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.filePath);
        return hash;
    }

    static class BSAFileRef {

        int size;
        long nameOffset;
        boolean flippedCompression;
        long dataOffset;
    }

    static class BSAFolder {

        String name;
        long dataPos;
        private int fileCount;
        Map<String, BSAFileRef> files = new HashMap<>();

        void setFileCount(int fileCount) {
            this.fileCount = fileCount;
            files = new HashMap<>(fileCount);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return filePath;
    }

    static void logBSAError(String source, Exception ex) {
        String error = "Could not get " + source + ". Strings files or ini changes in it will not be availible.";
        SPGlobal.logError(header, error);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        ex.printStackTrace(pw);
        pw.flush();
        sw.flush();
        SPGlobal.log(sw.toString());
    }

    /**
     * Enum containing all of the various filetypes BSAs could contain.
     */
    public enum FileType {

        /**
         *
         */
        NIF,
        /**
         *
         */
        DDS,
        /**
         *
         */
        XML,
        /**
         *
         */
        WAV,
        /**
         *
         */
        MP3,
        /**
         *
         */
        TXT_HTML_BAT_SCC,
        /**
         *
         */
        SPT,
        /**
         *
         */
        TEX_FNT,
        /**
         *
         */
        CTL
    }

    /**
     *
     */
    public static enum LogTypes {

        /**
         * A logstream used for logging which records have been skipped/blockec.
         */
        BSA;
    }

    /**
     *
     */
    public enum BSAFlag {

        /**
         *
         */
        DirectoriesHaveNames(0),
        /**
         *
         */
        FilesHaveNames(1),
        /**
         *
         */
        Compressed(2),
        /**
         *
         */
        NamesInFileData(8);
        int value;

        BSAFlag(int val) {
            value = val;
        }
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean is(BSAFlag flag) {
        return archiveFlags.get(flag.value);
    }

    boolean isCompressed(BSAFileRef ref) {
        boolean compressed = is(BSAFlag.Compressed);
        if (ref.flippedCompression) {
            compressed = !compressed;
        }
        return compressed;
    }
}
