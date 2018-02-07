package skyproc;

import java.util.Scanner;

/**
 * Non-public class intended to store bash tags for mods.
 *
 * @author Kurtis
 */
public class ModTags {

    //Generic deactivate tag.
    boolean Deactivate;
    //All initially declared false until import statements are processed.
    //Actor booleans.
    boolean ActorACBS = false;
    boolean ActorAIDT = false;
    boolean ActorAIPackages = false;
    boolean ActorAIPackagesForceAdd = false;
    boolean ActorAnimations = false;
    boolean ActorCombatStyle = false;
    boolean ActorDeathItem = false;
    boolean ActorSpells = false;
    boolean ActorSpellsForceAdd = false;
    boolean ActorStats = false;
    boolean ActorSkills = false;
    boolean ActorFactions = false;
    boolean ActorInventories = false;
    boolean ActorFaces = false;
    boolean ActorClass = false;
    boolean ActorEyesOnly = false;
    boolean ActorHairOnly = false;
    boolean ActorRace = false;
    boolean ActorFlags = false;
    boolean ActorTemplateFlags = false;
    boolean ActorPerks = false;
    boolean ActorOutfits = false;
    boolean ActorSkin = false;
    boolean ActorTemplates = false;
    boolean ActorVoice = false;
    //Race tags
    boolean RStatsF = false;
    boolean RStatsM = false;
    boolean BodySizeF = false;
    boolean BodySizeM = false;
    boolean BodyF = false;
    boolean BodyM = false;
    boolean eyes = false;
    boolean Hair = false;
    boolean RSpells = false;
    boolean REars = false;
    boolean RHead = false;
    boolean VoiceF = false;
    boolean VoiceM = false;
    boolean RBody = false;
    //More universal tags.
    boolean keywords = false;
    boolean scripts = false;
    boolean names = false;
    //Graphics tags.
    boolean Graphics = false;
    //Sounds
    boolean Sounds = false;
    boolean Stats = false;
    //Leveled Lists
    boolean Delev = false;
    //Data lock for imports, to lock and prevent anything from overriding it.  Normally off.
    boolean lockData = false;

    /**
     * Specialized constructor for tags.
     *
     * @param tags Constructor for a string list of tags, in the form of tag1,
     * tag2, tag3.. etc.
     */
    public ModTags(String tags) {
        Scanner s = new Scanner(tags);
        s.useDelimiter(",");

        while (s.hasNext()) {
            switch (s.next().trim()) {
                case "DEACTIVATE":
                    Deactivate = true;
                    break;
                case "Actor.ACBS":
                    ActorACBS = true;
                    break;
                case "Actor.AIDT":
                    ActorAIDT = true;
                    break;
                case "Actor.AIPackages":
                    ActorAIPackages = true;
                    break;
                case "Actor.AIPackagesForceAdd":
                    ActorAIPackagesForceAdd = true;
                    break;
                case "Actor.Animations":
                    ActorAnimations = true;
                    break;
                case "Actor.CombatStyle":
                    ActorCombatStyle = true;
                    break;
                case "Actor.DeathItem":
                    ActorDeathItem = true;
                    break;
                case "Actor.Spells":
                    ActorSpells = true;
                    break;
                case "Actor.SpellsForceAdd":
                    ActorSpellsForceAdd = true;
                    break;
                case "Actor.Stats":
                    ActorStats = true;
                    break;
                case "Actor.Skills":
                    ActorSkills = true;
                    break;
                case "Factions":
                    ActorFactions = true;
                    break;
                case "Invent":
                    ActorInventories = true;
                    break;
                case "NpcFaces":
                    ActorFaces = true;
                    break;
                case "names":
                    names = true;
                    break;
                case "NPC.Class":
                    ActorClass = true;
                    break;
                case "Npc.EyesOnly":
                    ActorEyesOnly = true;
                    break;
                case "Npc.HairOnly":
                    ActorHairOnly = true;
                    break;
                case "Scripts":
                    scripts = true;
                    break;
                case "Keywords":
                    keywords = true;
                    break;
                case "Actor.Flags":
                    ActorFlags = true;
                    break;
                case "Actor.TemplateFlags":
                    ActorTemplateFlags = true;
                    break;
                case "Actor.Perks":
                    ActorPerks = true;
                    break;
                case "Actor.Outfits":
                    ActorOutfits = true;
                    break;
                case "Actor.Skin":
                    ActorSkin = true;
                    break;
                case "Actor.Templates":
                    ActorTemplates = true;
                    break;
                case "Actor.Voices":
                    ActorVoice = true;
                    break;
                case "LockData":
                    lockData = true;
                    break;
                case "Sounds":
                    Sounds = true;
                    break;
                case "Stats":
                    Stats = true;
                    break;
                case "R.Attributes-F":
                    RStatsF = true;
                    break;
                case "R.Attributes-M":
                    RStatsM = true;
                    break;
                case "Body-Size-F":
                    BodySizeF = true;
                    break;
                case "Body-Size-M":
                    BodySizeM = true;
                    break;
                case "Body-F":
                    BodyF = true;
                    break;
                case "Body-M":
                    BodyM = true;
                    break;
                case "Eyes":
                    eyes = true;
                    break;
                case "Hair":
                    Hair = true;
                    break;
                case "R.AddSpells":
                    RSpells = true;
                    break;
                case "R.Ears":
                    REars = true;
                    break;
                case "R.Head":
                    RHead = true;
                    break;
                case "Voice-F":
                    VoiceF = true;
                    break;
                case "Voice-M":
                    VoiceM = true;
                    break;
                case "R.Body":
                    RBody = true;
                    break;
                case "Delev":
                    Delev = true;
                    break;
            }

            s.close();
        }
    }

    /**
     * Default mod tags constructor.
     *
     * @param use Constructor for activating all tags but deactivate and
     * override. Default for most mods.
     */
    public ModTags(boolean use) {
        if (use) {
            ActorACBS = true;
            ActorAIDT = true;
            ActorAIPackages = true;
            ActorAIPackagesForceAdd = true;
            ActorAnimations = true;
            ActorCombatStyle = true;
            ActorDeathItem = true;
            ActorSpells = true;
            ActorSpellsForceAdd = true;
            ActorStats = true;
            ActorSkills = true;
            ActorFactions = true;
            ActorInventories = true;
            ActorFaces = true;
            ActorClass = true;
            ActorEyesOnly = true;
            ActorHairOnly = true;
            ActorRace = true;
            ActorFlags = true;
            ActorTemplateFlags = true;
            ActorPerks = true;
            ActorOutfits = true;
            ActorSkin = true;
            ActorTemplates = true;
            ActorVoice = true;

            keywords = true;
            scripts = true;
            names = true;
            Graphics = true;
            Sounds = true;
            Stats = true;

            lockData = false;
            Deactivate = false;

            RStatsF = true;
            RStatsM = true;
            BodySizeF = true;
            BodySizeM = true;
            BodyF = true;
            BodyM = true;
            eyes = true;
            Hair = true;
            RSpells = true;
            REars = true;
            RHead = true;
            VoiceF = true;
            VoiceM = true;
            RBody = true;

            Delev = true;
        }
    }
}
