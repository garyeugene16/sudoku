/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import Agent.ManagerAgent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author garyeugene
 */
public class SudokuGUI extends javax.swing.JFrame {

    // Array 2D untuk menyimpan 81 kotak isian
    private JTextField[][] cells = new JTextField[9][9];

    // Label untuk status angka tersisa (1-9)
    private JLabel[] statusLabels = new JLabel[9];
    private boolean isProgrammaticUpdate = false;

    private final int[][] templateBoard = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    private ManagerAgent myAgent;

    public SudokuGUI() {
        this.myAgent = null;
        initComponents();    // Bikin panel & tombol
        initCustomGrid();    // Bikin 81 kotak angka
        initStatusPanel(); // Bikin status Baru
        initUIKustom();
    }

    public SudokuGUI(ManagerAgent agent) {
        this.myAgent = agent;
        initComponents(); // Bikin panel & tombol
        initCustomGrid();  // Bikin 81 kotak angka
        initStatusPanel(); // Bikin status Baru
        initUIKustom();
    }

    //Membuat 81 Kotak secara Otomatis
    private void initCustomGrid() {
        // Set layout 9x9 tanpa spasi antar komponen (gap = 0)
        PuzzlePanel.setLayout(new GridLayout(9, 9, 0, 0));

        Font font = new Font("Arial", Font.BOLD, 20);

        // Listener untuk mendeteksi input user secara realtime
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                calculateStatusRealtime();
            }

            public void removeUpdate(DocumentEvent e) {
                calculateStatusRealtime();
            }

            public void changedUpdate(DocumentEvent e) {
                calculateStatusRealtime();
            }
        };

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setFont(font);

                cells[i][j].getDocument().addDocumentListener(docListener);

                // Logika border 3x3
                // Jika baris/kolom kelipatan 3, berikan garis tebal (3px), selain itu tipis (1px)
                int top = (i % 3 == 0) ? 3 : 1;
                int left = (j % 3 == 0) ? 3 : 1;
                int bottom = (i == 8) ? 3 : 0; // Garis bawah hanya untuk baris terakhir
                int right = (j == 8) ? 3 : 0;  // Garis kanan hanya untuk kolom terakhir

                // Terapkan border warna hitam
                cells[i][j].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                // Warna background selang-seling
                if (((i / 3) + (j / 3)) % 2 == 0) {
                    cells[i][j].setBackground(new Color(245, 245, 245));
                } else {
                    cells[i][j].setBackground(Color.WHITE);
                }

                PuzzlePanel.add(cells[i][j]);
            }
        }
        PuzzlePanel.revalidate();
        PuzzlePanel.repaint();
    }

    private void initUIKustom() {
        //Reset Content Pane dan set Layout baru
        getContentPane().removeAll(); // Hapus layout bawaan NetBeans
        setLayout(new BorderLayout(10, 10));
        setSize(450, 650); // Perbesar ukuran window

        //Panel atas (Judul + Tombol Template/Import/Clear)
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        jLabel1.setFont(new Font("Arial", Font.BOLD, 24));
        jLabel1.setAlignmentX(CENTER_ALIGNMENT);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnTemplate = new JButton("Template");
        JButton btnImport = new JButton("Import Soal");
        JButton btnClear = new JButton("Hapus Papan");

        // Aksi Tombol
        btnTemplate.addActionListener(e -> loadBoardToGUI(templateBoard));
        btnImport.addActionListener(e -> actionImport());
        btnClear.addActionListener(e -> clearBoardGUI());

        menuPanel.add(btnTemplate);
        menuPanel.add(btnImport);
        menuPanel.add(btnClear);

        topContainer.add(Box.createVerticalStrut(10));
        topContainer.add(jLabel1);
        topContainer.add(menuPanel);

        add(topContainer, BorderLayout.NORTH);

        //Panel tengah (Papan Sudoku)
        // Kita gunakan ulang PuzzlePanel yang dibuat NetBeans
        PuzzlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(PuzzlePanel, BorderLayout.CENTER);

        //Panel BAWAH (Status Bar + Tombol Cek & Solve)
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));

        // -- Status Bar (Tabel)
        JPanel statusPanel = new JPanel(new GridLayout(2, 10));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Header Status
        addCellToStatus(statusPanel, "Angka", true);
        for (int i = 1; i <= 9; i++) {
            addCellToStatus(statusPanel, String.valueOf(i), true);
        }
        // Isi Status
        addCellToStatus(statusPanel, "Sisa", true);
        for (int i = 0; i < 9; i++) {
            statusLabels[i] = new JLabel("9");
            statusLabels[i].setHorizontalAlignment(JLabel.CENTER);
            statusLabels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            statusLabels[i].setOpaque(true);
            statusLabels[i].setBackground(Color.WHITE);
            statusPanel.add(statusLabels[i]);
        }

        //Tombol Aksi
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCheck = new JButton("Cek Jawaban");
        btnCheck.addActionListener(e -> actionCheck());

        // gunakan ulang btnSolve dari NetBeans agar event-nya tetap jalan
        btnSolve.setPreferredSize(new Dimension(150, 35));

        actionPanel.add(btnCheck);
        actionPanel.add(btnSolve); // btnSolve bawaan NetBeans dimasukkan ke sini

        bottomContainer.add(statusPanel);
        bottomContainer.add(actionPanel);
        bottomContainer.add(Box.createVerticalStrut(10));

        add(bottomContainer, BorderLayout.SOUTH);

        // Refresh layout
        revalidate();
    }

    // Helper kecil untuk tabel status
    private void addCellToStatus(JPanel p, String text, boolean isHeader) {
        JLabel l = new JLabel(text);
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if (isHeader) {
            l.setFont(l.getFont().deriveFont(Font.BOLD));
            l.setBackground(new Color(220, 220, 220));
            l.setOpaque(true);
        }
        p.add(l);
    }

    private void actionImport() {
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setDialogTitle("Pilih File Soal Sudoku (.txt)");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int[][] importedBoard = new int[9][9];
                Scanner scanner = new Scanner(fileChooser.getSelectedFile());
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (scanner.hasNextInt()) {
                            importedBoard[i][j] = scanner.nextInt();
                        } else {
                            if (scanner.hasNext()) {
                                scanner.next();
                            }
                            importedBoard[i][j] = 0;
                        }
                    }
                }
                scanner.close();
                loadBoardToGUI(importedBoard);
                JOptionPane.showMessageDialog(this, "Soal berhasil dimuat!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat file: " + e.getMessage());
            }
        }
    }

    // Logika untuk cek jawaban
    private void actionCheck() {
        boolean isValid = true;
        boolean isFull = true;
        int[][] tempBoard = new int[9][9];

        //Ambil data & Reset warna
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].isEditable()) {
                    cells[i][j].setBackground(Color.WHITE);
                }

                String txt = cells[i][j].getText().trim();
                if (txt.isEmpty()) {
                    tempBoard[i][j] = 0;
                    isFull = false;
                } else {
                    try {
                        tempBoard[i][j] = Integer.parseInt(txt);
                    } catch (Exception e) {
                        tempBoard[i][j] = 0;
                    }
                }
            }
        }

        //Validasi
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int val = tempBoard[i][j];
                if (val != 0) {
                    tempBoard[i][j] = 0; // Kosongkan diri sendiri utk cek
                    if (!isValidPlacement(tempBoard, i, j, val)) {
                        isValid = false;
                        cells[i][j].setBackground(new Color(255, 200, 200)); // Merah
                    }
                    tempBoard[i][j] = val; // Kembalikan
                }
            }
        }

        if (!isValid) {
            JOptionPane.showMessageDialog(this, "Ada kesalahan (kotak merah)!", "Cek", JOptionPane.WARNING_MESSAGE);
        } else if (!isFull) {
            JOptionPane.showMessageDialog(this, "Benar sejauh ini, tapi belum selesai.", "Cek", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "SELESAI! Jawaban Benar.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Helper buat validasi Sudoku Standard
    private boolean isValidPlacement(int[][] board, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num) {
                return false;
            }
            if (board[i][col] == num) {
                return false;
            }
        }
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    //Logika Status Bar & Update
    private void calculateStatusRealtime() {
        if (isProgrammaticUpdate) {
            return;
        }
        int[] counts = new int[10];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    String t = cells[i][j].getText().trim();
                    if (!t.isEmpty()) {
                        int v = Integer.parseInt(t);
                        if (v >= 1 && v <= 9) {
                            counts[v]++;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        updateStatusBarGUI(counts);
    }

    private void updateStatusBarGUI(int[] counts) {
        for (int k = 1; k <= 9; k++) {
            int rem = 9 - counts[k];
            if (rem < 0) {
                rem = 0;
            }
            statusLabels[k - 1].setText(String.valueOf(rem));
            statusLabels[k - 1].setBackground(rem == 0 ? new Color(144, 238, 144) : Color.WHITE);
        }
    }

    public void loadBoardToGUI(int[][] board) {
        isProgrammaticUpdate = true; // Matikan listener agar tidak berat
        btnSolve.setEnabled(true);
        btnSolve.setText("SOLVE (Robot)");
        int[] counts = new int[10];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int val = board[i][j];
                cells[i][j].setBackground(Color.WHITE);
                if (val != 0) {
                    cells[i][j].setText(String.valueOf(val));
                    cells[i][j].setEditable(false);
                    cells[i][j].setBackground(new Color(230, 230, 230));
                    cells[i][j].setForeground(Color.BLACK);
                    counts[val]++;
                } else {
                    cells[i][j].setText("");
                    cells[i][j].setEditable(true);
                    if (((i / 3) + (j / 3)) % 2 == 0) {
                        cells[i][j].setBackground(new Color(250, 250, 250));
                    }
                    cells[i][j].setForeground(Color.BLACK);
                }
            }
        }
        updateStatusBarGUI(counts);
        isProgrammaticUpdate = false;
    }

    public void clearBoardGUI() {
        isProgrammaticUpdate = true;
        btnSolve.setEnabled(true);
        btnSolve.setText("SOLVE (Robot)");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText("");
                cells[i][j].setEditable(true);
                cells[i][j].setBackground(Color.WHITE);
            }
        }
        updateStatusBarGUI(new int[10]);
        isProgrammaticUpdate = false;
    }

    private void initStatusPanel() {
        // Ubah Layout Frame utama menjadi BorderLayout agar rapi
        getContentPane().setLayout(new BorderLayout(10, 10));

        // Pindahkan komponen yang sudah ada ke posisi semestinya
        JPanel topPanel = new JPanel();
        topPanel.add(jLabel1);
        topPanel.add(btnSolve);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(PuzzlePanel, BorderLayout.CENTER);

        //Buat Panel Status Baru
        JPanel statusPanel = new JPanel();
        // Layout: 2 Baris (Judul & Isi), 10 Kolom (Label "Angka" + 9 Angka)
        statusPanel.setLayout(new GridLayout(2, 10));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.setPreferredSize(new Dimension(350, 60)); // Tinggi panel status

        //Baris 1: Header (Teks "Angka" dan 1-9)
        addCellToStatus(statusPanel, "Angka", true);
        for (int i = 1; i <= 9; i++) {
            addCellToStatus(statusPanel, String.valueOf(i), true);
        }

        //Baris 2: Nilai Tersisa (Teks "Tersisa" dan 0-9)
        addCellToStatus(statusPanel, "Tersisa", true);
        for (int i = 0; i < 9; i++) {
            statusLabels[i] = new JLabel("9"); // Awalnya semua tersisa 9
            statusLabels[i].setHorizontalAlignment(JLabel.CENTER);
            statusLabels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            statusLabels[i].setOpaque(true);
            statusLabels[i].setBackground(Color.WHITE);
            statusPanel.add(statusLabels[i]);
        }

        // Masukkan Panel Status ke Bawah (South)
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        // Refresh Frame agar layout baru terbaca
        pack();
    }

//    // Helper untuk membuat kotak di status bar
//    private void addCellToStatus(JPanel p, String text, boolean isHeader) {
//        JLabel l = new JLabel(text);
//        l.setHorizontalAlignment(JLabel.CENTER);
//        l.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        if (isHeader) {
//            l.setFont(l.getFont().deriveFont(Font.BOLD));
//            l.setBackground(new Color(220, 220, 220));
//            l.setOpaque(true);
//        }
//        p.add(l);
//    }
    // Update board
    public void updateBoardGUI(int[][] board) {
        SwingUtilities.invokeLater(() -> {
            isProgrammaticUpdate = true;
            int[] counts = new int[10];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int val = board[i][j];
                    if (val != 0) {
                        cells[i][j].setText(String.valueOf(val));
                        // Jika sel ini awalnya editable (berarti bukan soal), beri warna biru
                        if (cells[i][j].isEditable()) {
                            cells[i][j].setForeground(Color.BLUE);
                        }
                        if (val >= 1 && val <= 9) {
                            counts[val]++;
                        }
                    }
                }
            }
            updateStatusBarGUI(counts); // Update status
            isProgrammaticUpdate = false;
        });
    }

    //Menampilkan soal awal
    public void setInitialBoard(int[][] board) {
        int[] counts = new int[10];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int val = board[i][j];
                if (val != 0) {
                    cells[i][j].setText(String.valueOf(val));
                    cells[i][j].setEditable(false); // Soal tidak boleh diedit
                    cells[i][j].setBackground(Color.LIGHT_GRAY);
                    counts[val]++;
                } else {
                    cells[i][j].setText("");
                    cells[i][j].setEditable(true); // User boleh isi manual
                }
            }
        }

        // Update status bar awal
        for (int k = 1; k <= 9; k++) {
            int remaining = 9 - counts[k];
            statusLabels[k - 1].setText(String.valueOf(remaining));
            if (remaining == 0) {
                statusLabels[k - 1].setBackground(new Color(144, 238, 144));
            } else {
                statusLabels[k - 1].setBackground(Color.WHITE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnSolve = new javax.swing.JButton();
        PuzzlePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(350, 500));

        jLabel1.setText("SUDOKU");

        btnSolve.setText("Solve");
        btnSolve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PuzzlePanelLayout = new javax.swing.GroupLayout(PuzzlePanel);
        PuzzlePanel.setLayout(PuzzlePanelLayout);
        PuzzlePanelLayout.setHorizontalGroup(
            PuzzlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PuzzlePanelLayout.setVerticalGroup(
            PuzzlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 436, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(347, Short.MAX_VALUE)
                .addComponent(btnSolve)
                .addGap(15, 15, 15))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PuzzlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSolve)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PuzzlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSolveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolveActionPerformed
        // Ubah teks tombol agar user tahu proses berjalan
        btnSolve.setText("Robot sedang berpikir...");
        btnSolve.setEnabled(false);

        // Baca angka dari GUI ke array Integer
        int[][] currentBoard = new int[9][9];
        try {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    String text = cells[i][j].getText().trim();

                    if (text.isEmpty()) {
                        currentBoard[i][j] = 0; // Kalau kosong anggap 0
                    } else {
                        currentBoard[i][j] = Integer.parseInt(text);
                    }
                }
            }

            // Kirim data ke ManagerAgent untuk diproses
            if (myAgent != null) {
                myAgent.startSolvingFromGUI(currentBoard);
            } else {
                System.err.println("Error: GUI tidak terhubung ke Agent!");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input harus berupa angka 1-9!", "Error", JOptionPane.ERROR_MESSAGE);
            btnSolve.setEnabled(true);
            btnSolve.setText("SOLVE (Jalankan Robot)");
        }
    }//GEN-LAST:event_btnSolveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SudokuGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SudokuGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SudokuGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SudokuGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SudokuGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PuzzlePanel;
    private javax.swing.JButton btnSolve;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
