package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import java.text.MessageFormat;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kindredgroup.kps.internal.api.EnumValueSupplier;

// TODO:nikita.shvinagir:2023-01-30: consider using shared domain entities in all packages: persistence, messaging, etc.
public enum ContestType implements EnumValueSupplier {
    ALPINE_SKIING("AlpineSkiing"),
    ATHLETICS("Athletics"),
    AUSSIE_RULES("AussieRules"),
    BADMINTON("Badminton"),
    BANDY("Bandy"),
    BASEBALL("Baseball"),
    BASKETBALL("Basketball"),
    BASKETBALL_3X3("Basketball3x3"),
    BEACH_SOCCER("BeachSoccer"),
    BEACH_VOLLEYBALL("BeachVolleyball"),
    BIATHLON("Biathlon"),
    BOBSLEIGH("Bobsleigh"),
    BOWLS("Bowls"),
    BOXING("Boxing"),
    CHESS("Chess"),
    CRICKET("Cricket"),
    CROSS_COUNTRY("CrossCountry"),
    CURLING("Curling"),
    CYCLING("Cycling"),
    DARTS("Darts"),
    EBASKETBALL("EBasketball"),
    EICE_HOCKEY("EIceHockey"),
    ESOCCER("ESoccer"),
    ESPORT_ARENA_OF_VALOR("ESportArenaOfValor"),
    ESPORT_CALL_OF_DUTY("ESportCallOfDuty"),
    ESPORT_COUNTER_STRIKE("ESportCounterStrike"),
    ESPORT_DOTA("ESportDota"),
    ESPORT_KING_OF_GLORY("ESportKingOfGlory"),
    ESPORT_LEAGUE_OF_LEGENDS("ESportLeagueOfLegends"),
    ESPORT_OVERWATCH("ESportOverwatch"),
    ESPORT_RAINBOW_SIX("ESportRainbowSix"),
    ESPORT_ROCKET_LEAGUE("ESportRocketLeague"),
    ESPORT_STARCRAFT("ESportStarCraft"),
    ESPORT_VALORANT("ESportValorant"),
    ESPORT_WILD_RIFT("ESportWildRift"),
    FIELD_HOCKEY("FieldHockey"),
    FIGURE_SKATING("FigureSkating"),
    FLOORBALL("Floorball"),
    FOOTBALL("Football"),
    FORMULA_1("Formula1"),
    FORMULA_E("FormulaE"),
    FREESTYLE_SKIING("FreestyleSkiing"),
    FUTSAL("Futsal"),
    GAELIC_FOOTBALL("GaelicFootball"),
    GAELIC_HURLING("GaelicHurling"),
    GOLF("Golf"),
    GREYHOUNDS("Greyhounds"),
    HANDBALL("Handball"),
    HARNESS("Harness"),
    HEARTHSTONE("Hearthstone"),
    ICE_HOCKEY("IceHockey"),
    KABADDI("Kabaddi"),
    LACROSSE("Lacrosse"),
    LUGE("Luge"),
    MMA("Mma"),
    MOTORCYCLE_RACING("MotorcycleRacing"),
    NETBALL("Netball"),
    NORDIC_COMBINED("NordicCombined"),
    OLYMPICS("Olympics"),
    PADEL("Padel"),
    PESAPALLO("Pesapallo"),
    POOL("Pool"),
    RINK_HOCKEY("RinkHockey"),
    RUGBY("Rugby"),
    SAILING("Sailing"),
    SHORT_TRACK("ShortTrack"),
    SKELETON("Skeleton"),
    SKI_JUMPING("SkiJumping"),
    SNOOKER("Snooker"),
    SNOWBOARD("Snowboard"),
    SPEED_SKATING("SpeedSkating"),
    SPEEDWAY("Speedway"),
    SQUASH("Squash"),
    STOCK_CAR_RACING("StockCarRacing"),
    SURFING("Surfing"),
    SWIMMING("Swimming"),
    TABLE_TENNIS("TableTennis"),
    TENNIS("Tennis"),
    THOROUGHBRED("Thoroughbred"),
    VIRTUAL_GREYHOUNDS("VirtualGreyhounds"),
    VIRTUAL_HARNESS("VirtualHarness"),
    VIRTUAL_THOROUGHBRED("VirtualThoroughbred"),
    VOLLEYBALL("Volleyball"),
    WATER_POLO("Waterpolo");

    private final String value;

    ContestType(String value) {
        this.value = value;
    }

    public static ContestType of(String value) {
        return Arrays.stream(ContestType.values()).filter(type -> type.getValue().equals(value)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             MessageFormat.format("Value {0} is not supported by the ContestType", value)));
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }
}
