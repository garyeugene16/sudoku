/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import Agent.ManagerAgent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author garyeugene
 */
public class SudokuGUI extends javax.swing.JFrame {

    // Array 2D untuk menyimpan 81 kotak isian
    private JTextField[][] cells = new JTextField[9][9];
    
    // Label untuk status angka tersisa (1-9)
    private JLabel[] statusLabels = new JLabel[9];

    private ManagerAgent myAgent;

    public SudokuGUI() {
        this.myAgent = null;
        initComponents();    // Bikin panel & tombol
        initCustomGrid();    // Bikin 81 kotak angka
        initStatusPanel(); // Bikin status Baru
    }

    public SudokuGUI(ManagerAgent agent) {
        this.myAgent = agent;
        initComponents(); // Bikin panel & tombol
        initCustomGrid();  // Bikin 81 kotak angka
        initStatusPanel(); // Bikin status Baru
    }

    //Membuat 81 Kotak secara Otomatis
    private void initCustomGrid() {
        // 1. Ubah Panel menjadi layout Grid 9x9
        PuzzlePanel.setLayout(new GridLayout(9, 9));

        // 2. Loop untuk membuat 81 kotak isian
        Font font = new Font("Arial", Font.BOLD, 20);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // Bikin kotak baru
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER); // Teks di tengah
                cells[i][j].setFont(font);

                if (((i / 3) + (j / 3)) % 2 == 0) {
                    cells[i][j].setBackground(new Color(240, 240, 240));
                }

                // Masukkan kotak ke dalam PuzzlePanel
                PuzzlePanel.add(cells[i][j]);
            }
        }

        // Refresh tampilan agar kotak muncul
        PuzzlePanel.revalidate();
        PuzzlePanel.repaint();
    }
    
    private void initStatusPanel() {
        // 1. Ubah Layout Frame utama menjadi BorderLayout agar rapi
        // Kita reset layout bawaan NetBeans agar bisa menaruh panel di bawah
        getContentPane().setLayout(new BorderLayout(10, 10));
        
        // Pindahkan komponen yang sudah ada ke posisi semestinya
        JPanel topPanel = new JPanel();
        topPanel.add(jLabel1);
        topPanel.add(btnSolve);
        
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(PuzzlePanel, BorderLayout.CENTER);

        // 2. Buat Panel Status Baru
        JPanel statusPanel = new JPanel();
        // Layout: 2 Baris (Judul & Isi), 10 Kolom (Label "Angka" + 9 Angka)
        statusPanel.setLayout(new GridLayout(2, 10)); 
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.setPreferredSize(new Dimension(350, 60)); // Tinggi panel status

        // 3. Baris 1: Header (Teks "Angka" dan 1-9)
        addCellToStatus(statusPanel, "Angka", true);
        for (int i = 1; i <= 9; i++) {
            addCellToStatus(statusPanel, String.valueOf(i), true);
        }

        // 4. Baris 2: Nilai Tersisa (Teks "Tersisa" dan 0-9)
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
    
    // Helper untuk membuat kotak di status bar
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
    
    // Update board
    public void updateBoardGUI(int[][] board) {
        int[] counts = new int[10]; // Array hitung frekuensi angka 1-9
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int val = board[i][j];
                    if (board[i][j] != 0) {
                        cells[i][j].setText(String.valueOf(board[i][j]));
                        // Beri warna biru untuk angka yang ditemukan robot
                        cells[i][j].setForeground(Color.BLUE);
                        // Hitung jumlah angka yang muncul
                        if(val >= 1 && val <= 9) {
                            counts[val]++;
                        }
                    }
                }
            }
            for (int k = 1; k <= 9; k++) {
                int remaining = 9 - counts[k];
                if (remaining < 0) remaining = 0; // Jaga-jaga agar tidak negatif
                
                statusLabels[k-1].setText(String.valueOf(remaining));
                
                // Efek visual: Hijau jika sudah habis (0), Putih jika belum
                if (remaining == 0) {
                    statusLabels[k-1].setBackground(new Color(144, 238, 144)); // Hijau muda
                } else {
                    statusLabels[k-1].setBackground(Color.WHITE);
                }
            }
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
            statusLabels[k-1].setText(String.valueOf(remaining));
             if (remaining == 0) {
                    statusLabels[k-1].setBackground(new Color(144, 238, 144)); 
                } else {
                    statusLabels[k-1].setBackground(Color.WHITE);
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
        // 1. Ubah teks tombol agar user tahu proses berjalan
        btnSolve.setText("Robot sedang berpikir...");
        btnSolve.setEnabled(false);

        // 2. Baca angka dari GUI ke array Integer
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

            // 3. Kirim data ke ManagerAgent untuk diproses
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
