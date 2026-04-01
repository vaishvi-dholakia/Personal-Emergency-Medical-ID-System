import java.sql.*;
import java.util.*;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:medical.db";

    // ── initDatabase() ────────────────────────────────────────────
    // Creates both tables if they don't already exist.
    // Called once when the app starts.
    public static void initDatabase() {
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement()) {

            // Table 1: One row — the patient's basic info card
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS patient_record (" +
                            "  id                INTEGER PRIMARY KEY," +
                            "  name              TEXT NOT NULL," +
                            "  blood_group       TEXT NOT NULL," +
                            "  medication        TEXT NOT NULL," +
                            "  emergency_contact TEXT NOT NULL" +
                            ")"
            );

            // Table 2: Many rows — one per allergy treatment episode
            // AUTOINCREMENT means SQLite assigns id = 1, 2, 3... automatically
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS treatment_records (" +
                            "  id               INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  allergy          TEXT NOT NULL," +
                            "  hospital_name    TEXT NOT NULL," +
                            "  doctor_name      TEXT NOT NULL," +
                            "  doctor_contact   TEXT NOT NULL," +
                            "  treatment_date   TEXT NOT NULL," +
                            "  notes            TEXT," +
                            "  added_on         TEXT DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    // ── savePatient() ─────────────────────────────────────────────
    // Saves (or replaces) the basic patient card.
    // Always uses id = 1 so there is only ever ONE current card.
    public static void savePatient(Patient p) {
        String sql =
                "INSERT OR REPLACE INTO patient_record " +
                        "(id, name, blood_group, medication, emergency_contact) " +
                        "VALUES (1, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getBloodGroup());
            ps.setString(3, p.getMedication());
            ps.setString(4, p.getEmergencyContact());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("DB save patient error: " + e.getMessage());
        }
    }

    // ── loadPatient() ─────────────────────────────────────────────
    // Reads the current patient card (id = 1).
    // Returns null if no record exists yet.
    public static Patient loadPatient() {
        String sql = "SELECT * FROM patient_record WHERE id = 1";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new Patient(
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("medication"),
                        rs.getString("emergency_contact")
                );
            }
        } catch (SQLException e) {
            System.err.println("DB load patient error: " + e.getMessage());
        }
        return null;
    }

    // ── addTreatmentRecord() ──────────────────────────────────────
    // Inserts a new treatment episode into treatment_records.
    // Each call adds a NEW row — old records are never overwritten.
    public static void addTreatmentRecord(TreatmentRecord t) {
        String sql =
                "INSERT INTO treatment_records " +
                        "(allergy, hospital_name, doctor_name, doctor_contact, treatment_date, notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getAllergy());
            ps.setString(2, t.getHospitalName());
            ps.setString(3, t.getDoctorName());
            ps.setString(4, t.getDoctorContact());
            ps.setString(5, t.getTreatmentDate());
            ps.setString(6, t.getNotes());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("DB add treatment error: " + e.getMessage());
        }
    }

    // ── loadTreatmentRecords() ────────────────────────────────────
    // Returns ALL treatment records, newest first.
    // Each TreatmentRecord object represents one past episode.
    public static List<TreatmentRecord> loadTreatmentRecords() {
        String sql = "SELECT * FROM treatment_records ORDER BY id ";
        List<TreatmentRecord> list = new ArrayList<>();

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new TreatmentRecord(
                        rs.getInt("id"),
                        rs.getString("allergy"),
                        rs.getString("hospital_name"),
                        rs.getString("doctor_name"),
                        rs.getString("doctor_contact"),
                        rs.getString("treatment_date"),
                        rs.getString("notes") != null ? rs.getString("notes") : ""
                ));
            }
        } catch (SQLException e) {
            System.err.println("DB load treatments error: " + e.getMessage());
        }
        return list;
    }

    // ── connect() ─────────────────────────────────────────────────
    // Opens and returns a connection to medical.db.
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
