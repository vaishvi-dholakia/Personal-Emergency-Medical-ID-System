class TreatmentRecord {

    // ── Fields ────────────────────────────────────────────────────
    private int    id;              // auto-assigned by database (primary key)
    private String allergy;         // what allergy/reaction occurred
    private String hospitalName;    // where the patient was treated
    private String doctorName;      // which doctor treated them
    private String doctorContact;   // doctor's 10-digit phone number
    private String treatmentDate;   // date of treatment (DD/MM/YYYY)
    private String notes;           // any extra notes (optional)

    // ── Constructor (used when LOADING from database — id is known) ─
    public TreatmentRecord(int id, String allergy, String hospitalName,
                           String doctorName, String doctorContact,
                           String treatmentDate, String notes) {
        this.id            = id;
        this.allergy       = allergy;
        this.hospitalName  = hospitalName;
        this.doctorName    = doctorName;
        this.doctorContact = doctorContact;
        this.treatmentDate = treatmentDate;
        this.notes         = notes;
    }

    // ── Constructor (used when CREATING a new record — no id yet) ──
    // id will be assigned by SQLite AUTOINCREMENT
    public TreatmentRecord(String allergy, String hospitalName,
                           String doctorName, String doctorContact,
                           String treatmentDate, String notes) {
        this(0, allergy, hospitalName, doctorName, doctorContact, treatmentDate, notes);
    }

    // ── Getters ───────────────────────────────────────────────────
    public int    getId()            { return id; }
    public String getAllergy()       { return allergy; }
    public String getHospitalName()  { return hospitalName; }
    public String getDoctorName()    { return doctorName; }
    public String getDoctorContact() { return doctorContact; }
    public String getTreatmentDate() { return treatmentDate; }
    public String getNotes()         { return notes; }

    // ── validate() ────────────────────────────────────────────────
    // Returns an error message, or null if everything is valid.
    public String validate() {

        if (allergy.trim().isEmpty())
            return "Allergy / reaction cannot be empty.";

        if (hospitalName.trim().isEmpty())
            return "Hospital name cannot be empty.";
        if (!hospitalName.trim().matches("[a-zA-Z0-9 ,.-]{2,80}"))
            return "Hospital name contains invalid characters.";

        if (doctorName.trim().isEmpty())
            return "Doctor name cannot be empty.";
        if (!doctorName.trim().matches("[a-zA-Z .]{2,50}"))
            return "Doctor name must be letters only (2-50 characters).";

        if (!doctorContact.trim().matches("\\d{10}"))
            return "Doctor contact must be a 10-digit number.";

        // Date format: DD/MM/YYYY
        if (treatmentDate.trim().isEmpty())
            return "Treatment date cannot be empty.";
        if (!treatmentDate.trim().matches("\\d{2}/\\d{2}/\\d{4}"))
            return "Treatment date must be in DD/MM/YYYY format.";

        // Notes are optional — no validation needed

        return null; // all valid
    }

    // ── toString() ────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Allergy: "        + allergy       + "\n" +
                "Hospital: "       + hospitalName  + "\n" +
                "Doctor: "         + doctorName    + "\n" +
                "Doctor Contact: " + doctorContact + "\n" +
                "Date: "           + treatmentDate + "\n" +
                "Notes: "          + (notes.isEmpty() ? "—" : notes);
    }
}
