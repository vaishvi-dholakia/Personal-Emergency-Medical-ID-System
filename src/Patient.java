public class Patient {

    // ── Fields ────────────────────────────────────────────────────
    private String name;
    private String bloodGroup;
    private String medication;        // current ongoing medication
    private String emergencyContact;  // 10-digit number

    // ── Constructor ───────────────────────────────────────────────
    public Patient(String name, String bloodGroup,
                   String medication, String emergencyContact) {
        this.name             = name;
        this.bloodGroup       = bloodGroup;
        this.medication       = medication;
        this.emergencyContact = emergencyContact;
    }

    // ── Getters ───────────────────────────────────────────────────
    public String getName()             { return name; }
    public String getBloodGroup()       { return bloodGroup; }
    public String getMedication()       { return medication; }
    public String getEmergencyContact() { return emergencyContact; }

    // validate()
    public String validate() {

        if (name.trim().isEmpty())
            return "Name cannot be empty.";
        if (!name.trim().matches("[a-zA-Z ]{2,50}"))
            return "Name must be letters only (2-50 characters).";

        String[] validGroups = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
        boolean valid = false;
        for (String g : validGroups)
            if (g.equalsIgnoreCase(bloodGroup.trim())) { valid = true; break; }
        if (!valid)
            return "Blood group must be: A+, A-, B+, B-, AB+, AB-, O+, O-";

        if (medication.trim().isEmpty())
            return "Medication cannot be empty. Write 'None' if not applicable.";

        if (!emergencyContact.trim().matches("\\d{10}"))
            return "Emergency contact must be a 10-digit number.";

        return null; // all valid
    }

    // ── toString() ────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Name: "               + name             + "\n" +
                "Blood Group: "       + bloodGroup       + "\n" +
                "Medication: "        + medication       + "\n" +
                "Emergency Contact: " + emergencyContact;
    }
}