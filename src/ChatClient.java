import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter App");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8,40);

    public ChatClient(){
        //graphical UI
        /*setEditable(false) => until approval form chatServer client name unique or not,
        untill get approval nothing to do anythings*/

        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField,"North");
        frame.getContentPane().add(new JScrollPane(messageArea),"Center");
        frame.pack();

        //reset textfield empty after send msg
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

    }

    //to get server address
    private String getServerAddress(){
        return JOptionPane.showInputDialog(
                frame,
                "Enter the IP address of the server : ",
                "Welcome to the Gayathra's Chatter App",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    //to get screen name from user
    private String getName(){
        return JOptionPane.showInputDialog(
               frame,
                "Choose a screen name : ",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE
        );
    }


    private void run() throws IOException{
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress,9001);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),true);

        while(true){
            String line = in.readLine();

            if(line.startsWith("SUBMITNAME")){
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED!")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n" );
            }
        }
    }

    public static void main(String args[]) throws Exception{
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

}
