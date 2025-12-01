/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Agent;

import GUI.SudokuGUI;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author garyeugene
 */
public class ManagerAgent extends Agent {

    // Menyimpan data Papan Sudoku 9x9
    private int[][] board;

    private SudokuGUI myGui;

    // Daftar nama agen robot
    private String[] robotNames = {
        "Robot1", "Robot2", "Robot3", "Robot4", "Robot5",
        "Robot6", "Robot7", "Robot8", "Robot9"
    };

    @Override
    protected void setup() {
        System.out.println("Manager Board siap! (" + getLocalName() + ")");

        // 1. Inisialisasi Soal Sudoku (0 = Kosong)
        // Ini contoh soal level "Easy"
        board = new int[][]{
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

        // 2. TAMPILKAN GUI
        //kirim 'this' (agen manager sendiri) ke GUI agar GUI bisa memanggil kita balik
        myGui = new SudokuGUI(this);
        myGui.setInitialBoard(board); // Tampilkan board di layar
        myGui.setVisible(true);

        // 3. Mulai Mendengarkan Usulan (PROPOSE) dari Robot
        addBehaviour(new HandleProposalsBehaviour());
    }

    // Fungsi ini digunakan dan dipanggil oleh tombol "Solve" di GUI
    public void startSolvingFromGUI(int[][] currentBoardFromGUI) {
        System.out.println("Tombol ditekan! Mengambil alih papan dari GUI dan robot bekerja...");

        // Update data internal manager dengan apa yang ada di layar (termasuk input user)
        this.board = currentBoardFromGUI;

        // Sekarang baru broadcast ke robot!
        broadcastBoard("INFORM");
    }

    // Behaviour: Menangani pesan masuk dari Robot
    private class HandleProposalsBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            // Hanya terima pesan tipe PROPOSE
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Parsing Pesan: "row,col,number"
                String content = msg.getContent();
                String senderName = msg.getSender().getLocalName();

                try {
                    String[] parts = content.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    int number = Integer.parseInt(parts[2]);

                    // LOGIKA VALIDASI MANAGER:
                    // Cek 1: Apakah kotak masih kosong
                    if (board[row][col] == 0) {

                        // Cek 2: Update papan
                        board[row][col] = number;
                        System.out.println("TERIMA: " + senderName + " mengisi angka " + number + " di [" + row + "," + col + "]");

                        // --- UPDATE GUI SECARA REAL-TIME ---
                        if (myGui != null) {
                            myGui.updateBoardGUI(board);
                        }

                        // Cek 3: Apakah papan sudah penuh
                        if (isBoardFull()) {
                            System.out.println("SUDOKU SELESAI!");
                            printBoard();
                            broadcastBoard("TERMINATE"); // Suruh semua robot pulang
//                            myAgent.doDelete(); // Matikan diri sendiri
                        } else {
                            // Jika belum selesai, Broadcast papan terbaru ke semua orang
                            broadcastBoard("INFORM");
                        }

                    } else {
                        System.out.println("TOLAK: " + senderName + " terlambat, kotak [" + row + "," + col + "] sudah terisi.");
                    }

                } catch (Exception e) {
                    System.err.println("Error parsing pesan dari " + senderName);
                }

            } else {
                block(); // Tunggu pesan
            }
        }
    }

    // Mengirim status papan ke SEMUA robot (Robot1 - Robot9)
    private void broadcastBoard(String messageType) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        // Tambahkan semua penerima
        for (String name : robotNames) {
            msg.addReceiver(new AID(name, AID.ISLOCALNAME));
        }

        if (messageType.equals("TERMINATE")) {
            msg.setContent("TERMINATE");
        } else {
            // Ubah array 2D papan menjadi String panjang
            msg.setContent(boardToString(board));
        }

        send(msg);
    }

    // Mengubah int[][] menjadi String"
    private String boardToString(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sb.append(board[i][j]);
                if (i != 8 || j != 8) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }

    // Cek apakah papan sudah tidak ada angka 0
    private boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // Print papan ke terminal
    private void printBoard() {
        System.out.println("--- Hasil Akhir ---");
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("---------------------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print((board[i][j] == 0 ? "." : board[i][j]) + " ");
            }
            System.out.println();
        }
    }

}
