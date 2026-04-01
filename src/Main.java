// ── Main.java ──────────────────────────────────────────────────
// GUI + Event Handling — Personal Emergency Medical ID System
// 4 main functions:
//   1. View Emergency Card      — basic patient info from DB
//   2. Update Medical Info      — edit basic info (password protected)
//   3. Add Treatment Record     — log a new allergy+doctor+hospital episode
//   4. View Treatment History   — see all past treatment records in a table
// ──────────────────────────────────────────────────────────────

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*; //awt : abstract window toolkit
import java.util.List;

public class Main {

    // ── Colours ───────────────────────────────────────────────────
    static final Color BG          = new Color(245, 248, 255);
    static final Color WHITE       = Color.WHITE;
    static final Color BLUE        = new Color(30,  90, 200);
    static final Color BLUE_LIGHT  = new Color(70, 130, 230);
    static final Color BLUE_PALE   = new Color(219, 234, 254);
    static final Color BORDER_CLR  = new Color(191, 213, 250);
    static final Color RED_TEXT    = new Color(185,  28,  28);
    static final Color RED_SOFT    = new Color(254, 226, 226);
    static final Color TEXT_DARK   = new Color(15,  23,  55);
    static final Color TEXT_MID    = new Color(71,  85, 130);
    static final Color PURPLE      = new Color(109,  40, 217);
    static final Color GREEN       = new Color(21, 128,  61);
    static final Color GREEN_LIGHT = new Color(187, 247, 208);

    // ── Fonts ─────────────────────────────────────────────────────
    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  18);
    static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  12);
    static final Font F_VALUE = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_SMALL = new Font("Segoe UI", Font.ITALIC, 10);

    //  MAIN WINDOW

    public static void main(String[] args) {

        // Initialise DB and create tables on first run
        DatabaseHelper.initDatabase();

        JFrame frame = new JFrame("Personal Emergency Medical ID");
        frame.setSize(460, 430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG);

        // ── Header ────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setBackground(BLUE);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JLabel titleLbl = new JLabel("(+)  Personal Emergency Medical ID");
        titleLbl.setFont(F_TITLE);
        titleLbl.setForeground(WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("  Track allergies, treatments, doctors — all in one place.");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(new Color(180, 210, 255));
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(titleLbl);
        header.add(Box.createVerticalStrut(4));
        header.add(subLbl);
        frame.add(header, BorderLayout.NORTH);

        // ── 4 Main Buttons ────────────────────────────────────────
        JPanel center = new JPanel(new GridLayout(4, 1, 0, 10));
        center.setBackground(WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(24, 50, 24, 50));

        JButton viewBtn      = makeButton("View My Emergency Card",     BLUE,       WHITE);
        JButton updateBtn    = makeButton("Update Medical Information",  BLUE_LIGHT, WHITE);
        JButton treatmentBtn = makeButton("Add Treatment Record",        GREEN,      WHITE);
        JButton historyBtn   = makeButton("View Treatment History",      PURPLE,     WHITE);

        center.add(viewBtn);
        center.add(updateBtn);
        center.add(treatmentBtn);
        center.add(historyBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(18, 28, 0, 28));
        wrap.add(center);
        frame.add(wrap, BorderLayout.CENTER);

        // Exit button at bottom
        JButton exitBtn = new JButton("Exit Application");
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        exitBtn.setForeground(WHITE);
        exitBtn.setBackground(new Color(100, 116, 139)); // slate grey — always visible
        exitBtn.setOpaque(true);
        exitBtn.setBorderPainted(false);
        exitBtn.setFocusPainted(false);
        exitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitBtn.setBorder(BorderFactory.createEmptyBorder(7, 22, 7, 22));
        JPanel south = new JPanel();
        south.setBackground(BG);
        south.add(exitBtn);
        frame.add(south, BorderLayout.SOUTH);

        // ── Event Handling ────────────────────────────────────────

        // 1. View card — loads from DB and displays
        viewBtn.addActionListener(e -> showCard(frame));

        // 2. Update basic info — password protected
        updateBtn.addActionListener(e -> {
            JPasswordField pf = new JPasswordField(14);
            int result = JOptionPane.showConfirmDialog(frame,
                    new Object[]{"Enter password:", pf}, "Authentication",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                if (new String(pf.getPassword()).equals("vaishvi24"))
                    openUpdateForm(frame);
                else
                    JOptionPane.showMessageDialog(frame,
                            "Wrong password.", "Denied", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 3. Add a new treatment record
        treatmentBtn.addActionListener(e -> openTreatmentForm(frame));

        // 4. View all past treatment records
        historyBtn.addActionListener(e -> showTreatmentHistory(frame));

        // Exit
        exitBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(frame, "Exit the application?",
                    "Exit", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) System.exit(0);
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    // ════════════════════════════════════════════════════════════════
    //  1. VIEW EMERGENCY CARD
    // ════════════════════════════════════════════════════════════════
    static void showCard(JFrame parent) {

        Patient p = DatabaseHelper.loadPatient();

        JFrame win = new JFrame("Emergency Medical Card");
        win.setSize(400, 300);
        win.setResizable(false);
        win.setLayout(new BorderLayout());
        win.getContentPane().setBackground(BG);
        win.add(blueHeader("Emergency Medical Card"), BorderLayout.NORTH);

        if (p == null) {
            JPanel msg = new JPanel(new FlowLayout(FlowLayout.LEFT));
            msg.setBackground(WHITE);
            msg.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            JLabel err = new JLabel("No record found. Please update your information first.");
            err.setForeground(RED_TEXT);
            err.setFont(F_LABEL);
            msg.add(err);
            win.add(msg, BorderLayout.CENTER);
        } else {
            // Display basic patient info using Patient getters (OOP)
            JPanel body = new JPanel(new GridLayout(4, 2, 8, 12));
            body.setBackground(WHITE);
            body.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

            String[] labels = {"Name", "Blood Group", "Medication", "Emergency Contact"};
            String[] values = {p.getName(), p.getBloodGroup(),
                    p.getMedication(), p.getEmergencyContact()};

            for (int i = 0; i < labels.length; i++) {
                JLabel lbl = new JLabel(labels[i] + ":");
                lbl.setFont(F_LABEL);
                lbl.setForeground(TEXT_MID);

                JLabel val = new JLabel(values[i].isEmpty() ? "—" : values[i]);
                val.setFont(F_VALUE);
                val.setForeground(TEXT_DARK);

                body.add(lbl);
                body.add(val);
            }

            JPanel bodyWrap = new JPanel(new BorderLayout());
            bodyWrap.setBackground(BG);
            bodyWrap.setBorder(BorderFactory.createEmptyBorder(12, 14, 8, 14));
            bodyWrap.add(body);

            // Small hint below the card
            JLabel hint = new JLabel(
                    "  Allergy & treatment details → View Treatment History",
                    SwingConstants.CENTER);
            hint.setFont(F_SMALL);
            hint.setForeground(TEXT_MID);
            hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

            win.add(bodyWrap, BorderLayout.CENTER);
            win.add(hint, BorderLayout.SOUTH);
        }

        JButton closeBtn = makeButton("Close", BLUE_PALE, BLUE);
        closeBtn.setPreferredSize(new Dimension(120, 32));
        JPanel south = new JPanel();
        south.setBackground(BG);
        south.add(closeBtn);

        // Only add south panel if no hint label was added
        if (p != null) {
            // hint already added as SOUTH — put close inside CENTER-SOUTH indirectly
            // Rebuild as combined south panel
            JPanel combinedSouth = new JPanel(new BorderLayout());
            combinedSouth.setBackground(BG);
            JLabel hint2 = new JLabel(
                    "Allergy & treatment details → View Treatment History",
                    SwingConstants.CENTER);
            hint2.setFont(F_SMALL);
            hint2.setForeground(TEXT_MID);
            hint2.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            JPanel closeRow = new JPanel();
            closeRow.setBackground(BG);
            closeRow.add(closeBtn);
            combinedSouth.add(hint2, BorderLayout.NORTH);
            combinedSouth.add(closeRow, BorderLayout.SOUTH);
            win.add(combinedSouth, BorderLayout.SOUTH);
        } else {
            win.add(south, BorderLayout.SOUTH);
        }

        closeBtn.addActionListener(e -> win.dispose());
        win.setLocationRelativeTo(parent);
        win.setVisible(true);
    }


    // ════════════════════════════════════════════════════════════════
    //  2. UPDATE BASIC MEDICAL INFO FORM
    // ════════════════════════════════════════════════════════════════
    static void openUpdateForm(JFrame parent) {

        JFrame form = new JFrame("Update Medical Information");
        form.setSize(500, 360);
        form.setResizable(false);
        form.setLayout(new BorderLayout());
        form.getContentPane().setBackground(BG);
        form.add(blueHeader("Update Medical Information"), BorderLayout.NORTH);

        // Use BoxLayout so each row is label(fixed width) + field(full remaining width)
        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setBackground(WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(22, 28, 18, 28));

        String[] labels = {"Name", "Blood Group", "Medication", "Emergency Contact"};
        JTextField[] inputs = new JTextField[4];

        Patient existing = DatabaseHelper.loadPatient();
        String[] prefill = (existing != null)
                ? new String[]{existing.getName(), existing.getBloodGroup(),
                existing.getMedication(), existing.getEmergencyContact()}
                : new String[]{"", "", "", ""};

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i] + ":");
            lbl.setFont(F_LABEL);
            lbl.setForeground(TEXT_MID);
            lbl.setPreferredSize(new Dimension(160, 20));  // fixed label width

            inputs[i] = styledField(prefill[i]);

            // Each row: label on left, field takes all remaining space
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            row.add(lbl,       BorderLayout.WEST);
            row.add(inputs[i], BorderLayout.CENTER);

            fields.add(row);
            if (i < labels.length - 1)
                fields.add(Box.createVerticalStrut(12)); // gap between rows
        }

        JLabel hint = new JLabel(" ");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(RED_TEXT);
        hint.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setBackground(BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));
        centerPanel.add(fields, BorderLayout.CENTER);
        centerPanel.add(hint,   BorderLayout.SOUTH);
        form.add(centerPanel, BorderLayout.CENTER);

        JButton saveBtn  = makeButton("Save",  BLUE,      WHITE);
        JButton clearBtn = makeButton("Clear", BLUE_PALE, BLUE);
        saveBtn.setPreferredSize(new Dimension(120, 34));
        clearBtn.setPreferredSize(new Dimension(120, 34));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        south.setBackground(BG);
        south.add(saveBtn);
        south.add(clearBtn);
        form.add(south, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            // Build Patient object from inputs (OOP)
            Patient p = new Patient(
                    inputs[0].getText().trim(),
                    inputs[1].getText().trim(),
                    inputs[2].getText().trim(),
                    inputs[3].getText().trim()
            );
            String error = p.validate();
            if (error != null) { hint.setText("  " + error); return; }

            DatabaseHelper.savePatient(p);
            JOptionPane.showMessageDialog(form,
                    "Basic info saved!\n\n" + p.toString(), "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
            form.dispose();
        });

        clearBtn.addActionListener(e -> {
            for (JTextField f : inputs) f.setText("");
            hint.setText(" ");
            inputs[0].requestFocus();
        });

        form.setLocationRelativeTo(parent);
        form.setVisible(true);
    }


    // ════════════════════════════════════════════════════════════════
    //  3. ADD TREATMENT RECORD FORM
    //  A two-step feeling form:
    //  Step A — enter allergy/reaction
    //  Step B — enter where + who treated it + date + notes
    //  (Both steps are on the same window, just logically grouped)
    // ════════════════════════════════════════════════════════════════
    static void openTreatmentForm(JFrame parent) {

        JFrame form = new JFrame("Add Treatment Record");
        form.setSize(460, 490);
        form.setResizable(false);
        form.setLayout(new BorderLayout());
        form.getContentPane().setBackground(BG);
        form.add(blueHeader("Add Treatment Record"), BorderLayout.NORTH);

        // ── Section A label ───────────────────────────────────────
        JLabel secA = sectionLabel("STEP 1 — What allergy / reaction occurred?", RED_TEXT, RED_SOFT);

        // ── Section B label ───────────────────────────────────────
        JLabel secB = sectionLabel("STEP 2 — Where and who treated it?", GREEN, GREEN_LIGHT);

        // ── Input fields ──────────────────────────────────────────
        // Section A
        JTextField allergyField = styledField("");

        // Section B
        JTextField hospitalField  = styledField("");
        JTextField doctorField    = styledField("");
        JTextField contactField   = styledField("");
        JTextField dateField      = styledField(""); // DD/MM/YYYY
        JTextField notesField     = styledField(""); // optional

        // Build layout manually so we can insert section labels
        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setBackground(WHITE);
        formBody.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));

        // Section A
        formBody.add(labelledRow("Allergy / Reaction:", allergyField));
        formBody.add(Box.createVerticalStrut(8));

        // Section B
        formBody.add(labelledRow("Hospital / Clinic Name:", hospitalField));
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(labelledRow("Doctor Name:",            doctorField));
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(labelledRow("Doctor Contact:",         contactField));
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(labelledRow("Date of Treatment (DD/MM/YYYY):", dateField));
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(labelledRow("Notes (optional):",       notesField));

        JLabel hint = new JLabel(" ");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(RED_TEXT);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setBackground(BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));
        centerPanel.add(formBody, BorderLayout.CENTER);
        centerPanel.add(hint,     BorderLayout.SOUTH);
        form.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JButton saveBtn   = makeButton("Save Record", GREEN,      WHITE);
        JButton clearBtn  = makeButton("Clear",       BLUE_PALE,  BLUE);
        saveBtn.setPreferredSize(new Dimension(140, 34));
        clearBtn.setPreferredSize(new Dimension(100, 34));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        south.setBackground(BG);
        south.add(saveBtn);
        south.add(clearBtn);
        form.add(south, BorderLayout.SOUTH);

        // ── Event Handling: Save ──────────────────────────────────
        saveBtn.addActionListener(e -> {

            // Build TreatmentRecord object (OOP)
            TreatmentRecord t = new TreatmentRecord(
                    allergyField.getText().trim(),
                    hospitalField.getText().trim(),
                    doctorField.getText().trim(),
                    contactField.getText().trim(),
                    dateField.getText().trim(),
                    notesField.getText().trim()
            );

            // Validate using TreatmentRecord.validate()
            String error = t.validate();
            if (error != null) { hint.setText("  " + error); return; }

            // Save to database — adds a NEW row each time
            DatabaseHelper.addTreatmentRecord(t);
            JOptionPane.showMessageDialog(form,
                    "Treatment record saved!\n\n" + t.toString(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            form.dispose();
        });

        clearBtn.addActionListener(e -> {
            allergyField.setText("");
            hospitalField.setText("");
            doctorField.setText("");
            contactField.setText("");
            dateField.setText("");
            notesField.setText("");
            hint.setText(" ");
            allergyField.requestFocus();
        });

        form.setLocationRelativeTo(parent);
        form.setVisible(true);
    }


    // ════════════════════════════════════════════════════════════════
    //  4. VIEW TREATMENT HISTORY
    //  Shows all past treatment records in a scrollable JTable.
    // ════════════════════════════════════════════════════════════════
    static void showTreatmentHistory(JFrame parent) {

        JFrame win = new JFrame("Treatment History");
        win.setSize(820, 380);
        win.setResizable(true);
        win.setLayout(new BorderLayout());
        win.getContentPane().setBackground(BG);
        win.add(blueHeader("Treatment History"), BorderLayout.NORTH);

        // Load all treatment records from database
        List<TreatmentRecord> records = DatabaseHelper.loadTreatmentRecords();

        // JTable column headers
        String[] columns = {"#", "Allergy / Reaction", "Hospital", "Doctor",
                "Doctor Contact", "Date of Treatment", "Notes"};

        // DefaultTableModel holds the table data
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        if (records.isEmpty()) {
            model.addRow(new String[]{"—", "No treatment records yet.", "", "", "", "", ""});
        } else {
            // Add each TreatmentRecord as a row using its getters (OOP)
            for (TreatmentRecord t : records) {
                model.addRow(new Object[]{
                        t.getId(),
                        t.getAllergy(),
                        t.getHospitalName(),
                        t.getDoctorName(),
                        t.getDoctorContact(),
                        t.getTreatmentDate(),
                        t.getNotes().isEmpty() ? "—" : t.getNotes()
                });
            }
        }

        JTable table = new JTable(model);
        table.setFont(F_VALUE);
        table.setRowHeight(28);
        table.setGridColor(BORDER_CLR);
        table.setSelectionBackground(BLUE_PALE);
        table.setSelectionForeground(TEXT_DARK);

        // Style header row
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(F_LABEL);
        tableHeader.setBackground(BLUE);
        tableHeader.setForeground(WHITE);
        tableHeader.setReorderingAllowed(false);

        // Set column widths
        int[] widths = {30, 140, 140, 120, 110, 110, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(BG);
        tableWrap.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        tableWrap.add(scroll);
        win.add(tableWrap, BorderLayout.CENTER);

        JButton closeBtn = makeButton("Close", BLUE_PALE, BLUE);
        closeBtn.setPreferredSize(new Dimension(120, 32));
        JPanel south = new JPanel();
        south.setBackground(BG);
        south.add(closeBtn);
        win.add(south, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> win.dispose());
        win.setLocationRelativeTo(parent);
        win.setVisible(true);
    }


    // ════════════════════════════════════════════════════════════════
    //  UI HELPERS
    // ════════════════════════════════════════════════════════════════

    // Blue top header bar
    static JPanel blueHeader(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLUE);
        p.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        JLabel l = new JLabel(text);
        l.setFont(F_TITLE);
        l.setForeground(WHITE);
        p.add(l, BorderLayout.WEST);
        return p;
    }

    // Coloured section strip label (e.g. "STEP 1...")
    static JLabel sectionLabel(String text, Color fg, Color bg) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(5, 6, 5, 6));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // One label + input field row for the treatment form
    static JPanel labelledRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(F_LABEL);
        lbl.setForeground(TEXT_MID);
        lbl.setPreferredSize(new Dimension(210, 24));

        row.add(lbl,   BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    // Styled text input
    static JTextField styledField(String text) {
        JTextField f = new JTextField(text);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // slightly larger font
        f.setForeground(TEXT_DARK);
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 34)); // taller field
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10) // more vertical breathing room
        ));
        return f;
    }

    // Styled button
    static JButton makeButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(F_BTN);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return b;
    }
}