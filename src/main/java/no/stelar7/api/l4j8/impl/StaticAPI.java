package no.stelar7.api.l4j8.impl;

import no.stelar7.api.l4j8.basic.DataCall.DataCallBuilder;
import no.stelar7.api.l4j8.basic.constants.api.*;
import no.stelar7.api.l4j8.basic.constants.flags.*;
import no.stelar7.api.l4j8.pojo.staticdata.champion.*;
import no.stelar7.api.l4j8.pojo.staticdata.item.*;
import no.stelar7.api.l4j8.pojo.staticdata.language.LanguageStrings;
import no.stelar7.api.l4j8.pojo.staticdata.map.*;
import no.stelar7.api.l4j8.pojo.staticdata.mastery.*;
import no.stelar7.api.l4j8.pojo.staticdata.profileicon.*;
import no.stelar7.api.l4j8.pojo.staticdata.realm.Realm;
import no.stelar7.api.l4j8.pojo.staticdata.rune.*;
import no.stelar7.api.l4j8.pojo.staticdata.summonerspell.*;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unchecked")
public final class StaticAPI
{
    private static final StaticAPI INSTANCE = new StaticAPI();
    
    public static StaticAPI getInstance()
    {
        return StaticAPI.INSTANCE;
    }
    
    private StaticAPI()
    {
        // Hide public constructor
    }
    
    public Map<Integer, StaticChampion> getChampions(Platform server, @Nullable Set<ChampDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withURLData(Constants.URL_PARAM_DATA_BY_ID, String.valueOf(true))
                                                       .withEndpoint(URLEndpoint.V3_STATIC_CHAMPIONS)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.CHAMPLISTDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.CHAMPLISTDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        StaticChampionList list = (StaticChampionList) builder.build();
        return list.getData();
    }
    
    public StaticChampion getChampion(Platform server, int id, @Nullable Set<ChampDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withURLParameter(Constants.ID_PLACEHOLDER, String.valueOf(id))
                                                       .withEndpoint(URLEndpoint.V3_STATIC_CHAMPION_BY_ID)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.CHAMPDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.CHAMPDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        return (StaticChampion) builder.build();
    }
    
    public ItemList getItems(Platform server, @Nullable Set<ItemDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_ITEMS)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.ITEMLISTDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.ITEMLISTDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        return (ItemList) builder.build();
    }
    
    public Item getItem(Platform server, int id, @Nullable Set<ItemDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withURLParameter(Constants.ID_PLACEHOLDER, String.valueOf(id))
                                                       .withEndpoint(URLEndpoint.V3_STATIC_ITEM_BY_ID)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.ITEMDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.ITEMDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        return (Item) builder.build();
    }
    
    public Map<String, String> getLanguageStrings(Platform server, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_LANGUAGE_STRINGS)
                                                       .withPlatform(server);
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        LanguageStrings list = (LanguageStrings) builder.build();
        return list.getData();
    }
    
    /**
     * Returns a list of possible locales
     *
     * @param server the server to get from
     * @return a list of strings avaliable in this language
     */
    public List<String> getLanguages(Platform server)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_LANGUAGES)
                                                       .withPlatform(server);
        
        return (List<String>) builder.build();
    }
    
    public Map<String, MapDetails> getMaps(Platform server, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_MAPS)
                                                       .withPlatform(server);
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        MapData list = (MapData) builder.build();
        return list.getData();
    }
    
    public Map<Integer, StaticMastery> getMasteries(Platform server, @Nullable Set<MasteryDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_MASTERIES)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.MASTERYLISTDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.MASTERYLISTDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        StaticMasteryList list = (StaticMasteryList) builder.build();
        return list.getData();
    }
    
    public Map<String, List<MasteryTreeList>> getMasteryTree(Platform server, @Nullable Set<MasteryDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_MASTERIES)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.MASTERYLISTDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.MASTERYLISTDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        StaticMasteryList list = (StaticMasteryList) builder.build();
        return list.getTree();
    }
    
    public StaticMastery getMastery(Platform server, int id, @Nullable Set<MasteryDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withURLParameter(Constants.ID_PLACEHOLDER, String.valueOf(id))
                                                       .withEndpoint(URLEndpoint.V3_STATIC_MASTERY_BY_ID)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.MASTERYDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.MASTERYDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        return (StaticMastery) builder.build();
    }
    
    public Map<Long, ProfileIconDetails> getProfileIcons(Platform server, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_PROFILEICONS)
                                                       .withPlatform(server);
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        ProfileIconData list = (ProfileIconData) builder.build();
        return list.getData();
    }
    
    public Realm getRealm(Platform server)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_REALMS)
                                                       .withPlatform(server);
        return (Realm) builder.build();
    }
    
    public Map<Integer, StaticRune> getRunes(Platform server, @Nullable Set<RuneDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_RUNES)
                                                       .withPlatform(server);
        
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.RUNELISTDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.RUNELISTDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        StaticRuneList list = (StaticRuneList) builder.build();
        return list.getData();
    }
    
    
    public StaticRune getRune(Platform server, int id, @Nullable Set<RuneDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withURLParameter(Constants.ID_PLACEHOLDER, String.valueOf(id))
                                                       .withEndpoint(URLEndpoint.V3_STATIC_RUNE_BY_ID)
                                                       .withPlatform(server);
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.RUNEDATA_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.RUNEDATA_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        return (StaticRune) builder.build();
    }
    
    
    public Map<String, StaticSummonerSpell> getSummonerSpells(Platform server, @Nullable Set<SpellDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_SUMMONER_SPELLS)
                                                       .withPlatform(server);
        
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.SUMMONERSPELLLIST_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.SUMMONERSPELLLIST_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        StaticSummonerSpellList list = (StaticSummonerSpellList) builder.build();
        return list.getData();
    }
    
    public StaticSummonerSpell getSummonerSpell(Platform server, int id, @Nullable Set<SpellDataFlags> dataFlags, @Nullable String version, @Nullable String locale)
    {
        DataCallBuilder builder = new DataCallBuilder().withURLParameter(Constants.ID_PLACEHOLDER, String.valueOf(id))
                                                       .withEndpoint(URLEndpoint.V3_STATIC_SUMMONER_SPELL_BY_ID)
                                                       .withPlatform(server);
        
        
        if (dataFlags != null)
        {
            dataFlags.forEach(flag -> builder.withURLDataAsSet(Constants.SUMMONERSPELL_PLACEHOLDER_DATA, flag.getValue()));
        } else
        {
            builder.withURLDataAsSet(Constants.SUMMONERSPELL_PLACEHOLDER_DATA, ChampDataFlags.ALL.getValue());
        }
        
        
        if (version != null)
        {
            builder.withURLData(Constants.VERSION_PLACEHOLDER_DATA, version);
        }
        if (locale != null)
        {
            builder.withURLData(Constants.LOCALE_PLACEHOLDER_DATA, locale);
        }
        
        
        return (StaticSummonerSpell) builder.build();
    }
    
    
    public List<String> getVersions(Platform server)
    {
        DataCallBuilder builder = new DataCallBuilder().withEndpoint(URLEndpoint.V3_STATIC_VERSIONS)
                                                       .withPlatform(server);
        
        return (List<String>) builder.build();
    }
    
}
