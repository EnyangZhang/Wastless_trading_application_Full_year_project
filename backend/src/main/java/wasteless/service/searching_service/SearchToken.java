package wasteless.service.searching_service;

public class SearchToken {

    /** The search term itself, so either a word or a string of characters (including whitespace) of quotes */
    private final String term;

    /** Whether the term is a normal word or a series of characters in quotes (in which case we only want exact matches) */
    private final Boolean inQuotes;

    public SearchToken(String term, Boolean inQuotes) {
        this.term = term;
        this.inQuotes = inQuotes;
    }

    public String getTerm() {
        return term;
    }

    public Boolean inQuotes() {
        return inQuotes;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof SearchToken)) {
            return false;
        }

        return (((SearchToken) o).term.equals(this.term)) && (((SearchToken) o).inQuotes == this.inQuotes);
    }

}
