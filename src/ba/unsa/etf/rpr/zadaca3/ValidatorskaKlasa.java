package ba.unsa.etf.rpr.zadaca3;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ValidatorskaKlasa {

    public static boolean daLiJeValidanString(String n) {
        if (n.length() < 1)
            return false;
        return true;
    }

    public static boolean daLiJeValidanDatum(String s) {
        DateFormat format = new SimpleDateFormat("M/d/yyyy");
        format.setLenient(false);

        try {
            Date t = format.parse(s);
            //System.out.println(t);
            if (t.compareTo(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())) != -1)
                return false;
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    private static boolean cifraCheck(String broj) {
        boolean validno = false;

        char[] charovi = broj.toCharArray();
        for (int i = 0; i < charovi.length; i++) {
            validno = false;
            if (((charovi[i] >= '0') && (charovi[i] <= '9'))) {
                validno = true;
            }
        }
        return validno;
    }

    public static boolean daLiJeIspravanJMBG(String s) {
        // Nadjedno na netu
        List<Integer> lista = new ArrayList<Integer>();
        if (cifraCheck(s)) {
            for (char ch : s.toCharArray()) {
                lista.add(Integer.valueOf(String.valueOf(ch)));
            }

            if (lista.size() != 13) {
                return false;
            } else {
                int eval = 0;
                for (int i = 0; i < 6; i++) {
                    eval += (7 - i) * (lista.get(i) + lista.get(i + 6));
                }
                return lista.get(12) == 11 - eval % 11;
            }
        } else {
            return false;
        }
    }

    public static boolean validirajJmbgDatum(String jmbg, LocalDate datumRodjenja) {
        if (daLiJeIspravanJMBG(jmbg) && datumRodjenja != null) {
            int dan = Integer.parseInt(jmbg.substring(0, 2));
            int mjesec = Integer.parseInt(jmbg.substring(2, 4));
            String tempGodina = jmbg.substring(4, 7);
            int godina = 0;

            if (tempGodina.charAt(0) == '0') {
                godina = Integer.parseInt(tempGodina) + 2000;
            } else {
                godina = Integer.parseInt(tempGodina) + 1000;
            }
            return (dan == datumRodjenja.getDayOfMonth() && mjesec == datumRodjenja.getMonthValue() &&
                    godina == datumRodjenja.getYear());
        }
        return false;
    }

}
