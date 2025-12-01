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
            System.err.println("Error: Robot Angka butuh argumen (contoh: 1).");
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
            // Hanya menerima pesan tipe INFORM (Pemberitahuan Papan)
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                String content = msg.getContent(); // Papan dalam bentuk String (CSV)

                // Jika pesan berisi perintah "TERMINATE", agen mati
                if (content.equalsIgnoreCase("TERMINATE")) {
                    System.out.println("Robot " + myNumber + ": Tugas selesai. Pulang.");
                    myAgent.doDelete();
                    return;
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

    // Fungsi untuk mencari posisi yang VALID untuk 'myNumber'
    private void proposeMove(int[][] board, ACLMessage originalMsg) {
        // Loop seluruh papan
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {

                // Jika kotak ini kosong (0)
                if (board[row][col] == 0) {

                    // Cek apakah angka SAYA (myNumber) valid ditaruh di sini?
                    if (isValidMove(board, row, col, myNumber)) {

                        // LOGIKA TAMBAHAN (PENTING):
                        // Agar tidak bentrok, kita bisa pakai logika sederhana dulu:
                        // "Jika valid, langsung ajukan."
                        // (Nanti Manager yang memutuskan terima/tolak).
                        // Kirim pesan PROPOSE ke Manager
                        ACLMessage reply = originalMsg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);

                        // Format Pesan: "row,col,number" (misal: "0,5,9")
                        reply.setContent(row + "," + col + "," + myNumber);

                        System.out.println("Robot " + myNumber + " mengajukan posisi: [" + row + "," + col + "]");

                        send(reply);

                        // Opsional: Langsung return agar tidak spamming banyak proposal sekaligus
                        // Agen akan menunggu update papan berikutnya.
                        return;
                    }
                }
            }
        }
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
