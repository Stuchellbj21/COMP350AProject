public enum Major {
        ACCT, ART, ASTR, BIOL, CHEM, CMIN, COMM, COMP, DESI, ECON, EDUC, ELEE, ENGL, ENGR, ENTR, EXER,
        FNCE, FREN, GEOL, GREK, HEBR, HIST, HUMA, INBS, MARK, MATH, MECE, MNGT, MUSI, NURS, PHIL, PHYE,
        PHYS, POLS, PSYC, RELI, ROBO, SCIC, SEDU, SOCI, SOCW, SPAN, SSFT, THEA, WRIT, LATN;

        public static boolean is_major(String s) {
                try {
                        Major.valueOf(s);
                        return true;
                }
                catch(IllegalArgumentException iae) {return false;}
        }
}