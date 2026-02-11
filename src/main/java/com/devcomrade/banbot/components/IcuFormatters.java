package com.devcomrade.banbot.components;

import com.devcomrade.banbot.service.LocalizationService;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.text.MeasureFormat;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.Measure;
import com.ibm.icu.util.MeasureUnit;
import com.ibm.icu.util.ULocale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class IcuFormatters {
    private final LocalizationService localizationService;

    public String formatDurationSeconds(long seconds, Locale locale) {
        return formatDurationSeconds(seconds, locale, false);
    }

    public String formatDurationSeconds(long seconds, Locale locale, boolean na) {
        final long value;
        final MeasureUnit unit;

        if (seconds < 60) {
            value = seconds;
            unit = MeasureUnit.SECOND;
        } else if (seconds < 3600) {
            value = seconds / 60;
            unit = MeasureUnit.MINUTE;
        } else {
            value = seconds / 3600;
            unit = MeasureUnit.HOUR;
        }

        if (na) {
            return formatDurationForNa(value, unit, locale);
        } else {
            var fmt = MeasureFormat.getInstance(
                    ULocale.forLocale(locale),
                    MeasureFormat.FormatWidth.WIDE
            );

            return fmt.format(new Measure(value, unit));
        }
    }

    private String formatDurationForNa(long value, MeasureUnit unit, Locale locale) {
        // Русский: делаем винительный (секунду/минуту/час)
        if ("ru".equalsIgnoreCase(locale.getLanguage())) {
            return formatRuAccusative(value, unit);
        }

        // Остальные языки: ICU как было
        return formatDurationSeconds(value, locale, false);
    }

    private String formatRuAccusative(long value, MeasureUnit unit) {
        if (unit.equals(MeasureUnit.SECOND)) {
            return value + " " + pluralRu(value, "секунду", "секунды", "секунд");
        } else if (unit.equals(MeasureUnit.MINUTE)) {
            return value + " " + pluralRu(value, "минуту", "минуты", "минут");
        } else if (unit.equals(MeasureUnit.HOUR)) {
            return value + " " + pluralRu(value, "час", "часа", "часов");
        }

        throw new IllegalArgumentException("Unsupported unit: " + unit);
    }

    private String pluralRu(long n, String one, String few, String many) {
        long mod10 = n % 10;
        long mod100 = n % 100;
        if (mod10 == 1 && mod100 != 11) return one;
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 10 || mod100 >= 20)) return few;
        return many;
    }

    public String formatVotes(long count, Locale locale, String lang) {
        var uloc = ULocale.forLocale(locale);
        var rules = PluralRules.forLocale(uloc);
        var category = rules.select(count); // one/few/many/other

        var word = localizationService.getMessage(lang, "votes." + category);
        var num = NumberFormatter.withLocale(uloc).format(count).toString();
        return num + " " + word;
    }
}