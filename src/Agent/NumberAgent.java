/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Agent;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author garyeugene
 */
public class NumberAgent extends Agent {

    // Identitas: Angka berapa yang dipegang robot ini (1-9)
    private int myNumber;

    @Override
    protected void setup() {
        // 1. Mengambil Argumen saat Start (menentukan dia angka berapa)
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            try {
                myNumber = Integer.parseInt(args[0].toString());
                System.out.println("Robot Angka " + myNumber + " siap bekerja! (" + getLocalName() + ")");
            } catch (NumberFormatException e) {
                System.err.println("Error: Argumen harus berupa angka integer 1-9.");
                doDelete();
                return;
            }
        } else {
            System.err.println("Error: Robot Angka butuh argumen.");
            doDelete();
            return;
        }

        // 2. Menambahkan Perilaku (Behaviour) untuk mendengarkan pesan dari Manager
        addBehaviour(new AnalyzeBoardBehaviour());
    }

    // Behaviour: Selalu mendengarkan pesan berisi update Papan
    private class AnalyzeBoardBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            // Hanya menerima pesan tipe INFORM
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                String content = msg.getContent(); // Papan dalam bentuk String (CSV)

                // Jika pesan berisi perintah "TERMINATE", agen mati
//                if (content.equalsIgnoreCase("TERMINATE")) {
//                    System.out.println("Robot " + myNumber + ": Tugas selesai.");
//                    myAgent.doDelete();
//                    return;
//                }
                if (content.length() > 20) { // Papan sudoku string-nya panjang
                    System.out.println("Robot " + myNumber + " menerima papan baru...");
                    int[][] board = parseBoard(content);
                    proposeMove(board, msg);
                } else {
                    // Abaikan pesan pendek (misal pesan "DONE" atau sampah)
                }

                System.out.println("Robot " + myNumber + " menerima papan baru, sedang memindai...");

                // Parsing papan dan cari langkah
                int[][] board = parseBoard(content);
                proposeMove(board, msg);

            } else {
                block(); // Tunggu sampai ada pesan masuk
            }
        }
    }

//    // Fungsi untuk mencari posisi yang VALID untuk 'myNumber'
//    private void proposeMove(int[][] board, ACLMessage originalMsg) {
//        // Loop seluruh papan
//        for (int row = 0; row < 9; row++) {
//            for (int col = 0; col < 9; col++) {
//
//                // Jika kotak ini kosong (0)
//                if (board[row][col] == 0) {
//
//                    // Cek apakah angka SAYA (myNumber) valid ditaruh di sini?
//                    if (isValidMove(board, row, col, myNumber)) {
//
//                        // LOGIKA TAMBAHAN (PENTING):
//                        // Agar tidak bentrok, kita bisa pakai logika sederhana dulu:
//                        // "Jika valid, langsung ajukan."
//                        // (Nanti Manager yang memutuskan terima/tolak).
//                        // Kirim pesan PROPOSE ke Manager
//                        ACLMessage reply = originalMsg.createReply();
//                        reply.setPerformative(ACLMessage.PROPOSE);
//
//                        // Format Pesan: "row,col,number" (misal: "0,5,9")
//                        reply.setContent(row + "," + col + "," + myNumber);
//
//                        System.out.println("Robot " + myNumber + " mengajukan posisi: [" + row + "," + col + "]");
//
//                        send(reply);
//
//                        // Opsional: Langsung return agar tidak spamming banyak proposal sekaligus
//                        // Agen akan menunggu update papan berikutnya.
//                        return;
//                    }
//                }
//            }
//        }
//    }
    //Mencari langkah
    private void proposeMove(int[][] board, ACLMessage originalMsg) {

        // Cara 1: Hidden Single
        // Cek setiap baris, kolom, dan blok(3x3).
        // Cek apakah ada kotak kosong yang hanya bisa diisi oleh angka tsb
        // Artinya angka lain tidak valid di situ
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // Jika kotak kosong
                if (board[row][col] == 0) {

                    // 1. Cek dulu apakah angka boleh diletakkan di sini?
                    if (isValidMove(board, row, col, myNumber)) {

                        // 2. Cek apakah ini khusus angka ini?
                        // (Cek apakah angka lain 1-9 bisa masuk sini?)
                        if (isOnlyMeValidHere(board, row, col)) {
                            sendProposal(originalMsg, row, col);
                            return;
                        }
                    }
                }
            }
        }

        // Cara 2: Naked Single (Fallback)
        //bekerja dengan cara fokus pada satu kotak tertentu jadi tidak ada saingan angka lain di kotak itu.
        // 2.A. Cek per BARIS
        for (int r = 0; r < 9; r++) {
            // Cek apakah baris ini sudah punya angka myNumber
            boolean hasMyNum = false;
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == myNumber) {
                    hasMyNum = true;
                    break;
                }
            }

            if (!hasMyNum) {
                // Baris ini belum punya angka myNumber. Hitung ada berapa tempat valid.
                int validCount = 0;
                int targetCol = -1;

                for (int c = 0; c < 9; c++) {
                    if (board[r][c] == 0 && isValidMove(board, r, c, myNumber)) {
                        validCount++;
                        targetCol = c;
                    }
                }

                // Cuma ada 1 tempat di baris ini yang muat buat angka myNumber.
                if (validCount == 1) {
                    sendProposal(originalMsg, r, targetCol);
                    return;
                }
            }
        }

        // 2.B. Cek per KOLOM
        for (int c = 0; c < 9; c++) {
            // Cek apakah kolom ini sudah punya angka myNumber
            boolean hasMyNum = false;
            for (int r = 0; r < 9; r++) {
                if (board[r][c] == myNumber) {
                    hasMyNum = true;
                    break;
                }
            }

            if (!hasMyNum) {
                int validCount = 0;
                int targetRow = -1;

                for (int r = 0; r < 9; r++) {
                    if (board[r][c] == 0 && isValidMove(board, r, c, myNumber)) {
                        validCount++;
                        targetRow = r;
                    }
                }

                // Cuma ada 1 tempat di baris ini yang muat buat angka myNumber.
                if (validCount == 1) {
                    sendProposal(originalMsg, targetRow, c);
                    return;
                }
            }
        }

        // 2.C Cek per BLOK 3x3
        // Loop untuk setiap blok 3x3 (total ada 9 blok)
        for (int blockRow = 0; blockRow < 9; blockRow += 3) {
            for (int blockCol = 0; blockCol < 9; blockCol += 3) {

                // Cek 1: Apakah blok ini sudah punya angka saya?
                boolean hasMyNum = false;
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        if (board[blockRow + r][blockCol + c] == myNumber) {
                            hasMyNum = true;
                            break;
                        }
                    }
                }

                // Cek 2: Jika belum, cari posisi valid di dalam blok ini
                if (!hasMyNum) {
                    int validCount = 0;
                    int targetRow = -1;
                    int targetCol = -1;

                    for (int r = 0; r < 3; r++) {
                        for (int c = 0; c < 3; c++) {
                            int actualRow = blockRow + r;
                            int actualCol = blockCol + c;

                            // Cek apakah kotak ini kosong & valid
                            if (board[actualRow][actualCol] == 0 && isValidMove(board, actualRow, actualCol, myNumber)) {
                                validCount++;
                                targetRow = actualRow;
                                targetCol = actualCol;
                            }
                        }
                    }

                    // EUREKA! Cuma ada 1 tempat di blok ini yang muat buat saya.
                    if (validCount == 1) {
                        sendProposal(originalMsg, targetRow, targetCol);
                        return;
                    }
                }
            }
        }
    }

    // --- Helper Baru: Cek Deduksi ---
    private boolean isOnlyMeValidHere(int[][] board, int row, int col) {
        // Cek semua angka lain (selain myNumber)
        for (int otherNum = 1; otherNum <= 9; otherNum++) {
            if (otherNum != myNumber) {
                // Apakah angka lain itu valid ditaruh di sini?
                if (isValidMove(board, row, col, otherNum)) {
                    return false; // berarti, angka lain juga bisa, artinya bukan Hidden Single.
                }
            }
        }
        return true; // Cuma angka myNumber yang bisa masuk sini.
    }

    private void sendProposal(ACLMessage originalMsg, int row, int col) {
        ACLMessage reply = originalMsg.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        reply.setContent(row + "," + col + "," + myNumber);
        System.out.println("Robot " + myNumber + " menemukan langkah PASTI di [" + row + "," + col + "]");
        send(reply);
    }

    // 1. Cek apakah angka valid di posisi tersebut (Aturan Sudoku standar)
    private boolean isValidMove(int[][] board, int row, int col, int number) {
        // Cek Baris
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == number) {
                return false;
            }
        }

        // Cek Kolom
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == number) {
                return false;
            }
        }

        // Cek Kotak 3x3
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i + startRow][j + startCol] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    // 2. Mengubah String menjadi Array 2D
    private int[][] parseBoard(String content) {
        int[][] board = new int[9][9];
        String[] tokens = content.split(",");

        if (tokens.length != 81) {
            System.err.println("Error: Format papan salah (harus 81 angka)");
            return board;
        }

        int k = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    board[i][j] = Integer.parseInt(tokens[k].trim());
                } catch (NumberFormatException e) {
                    board[i][j] = 0;
                }
                k++;
            }
        }
        return board;
    }
}
