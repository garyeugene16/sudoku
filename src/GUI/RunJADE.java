package GUI;

public class RunJADE {
    public static void main(String[] args) {

        jade.Boot.main(new String[]{
            "-gui",
            "-agents",
            "Manager:Agent.ManagerAgent;" +
            "Robot1:Agent.NumberAgent(1);" +
            "Robot2:Agent.NumberAgent(2);" +
            "Robot3:Agent.NumberAgent(3);" +
            "Robot4:Agent.NumberAgent(4);" +
            "Robot5:Agent.NumberAgent(5);" +
            "Robot6:Agent.NumberAgent(6);" +  // sudah diperbaiki
            "Robot7:Agent.NumberAgent(7);" +
            "Robot8:Agent.NumberAgent(8);" +
            "Robot9:Agent.NumberAgent(9)"
        });

    }
}
