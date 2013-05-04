package client;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ChatBox extends JFrame {

    JTextArea display;
    JTextArea message;
    JScrollPane displayScroll;
    JScrollPane messageScroll;
    ChatBoxModel model;
    
    public ChatBox(ChatClientModel chatClientModel, int conversationID, String title) {
    	this.model = new ChatBoxModel(chatClientModel, this, conversationID);
        this.setSize(300, 300);
        
        display = new JTextArea();
        display.setEditable(false);
        display.setLineWrap(true);
        displayScroll = new JScrollPane(display);
        
        message = new JTextArea();
        message.setLineWrap(true);
        messageScroll = new JScrollPane(message);
        message.addKeyListener(model);
        
        this.setTitle(title);

        createGroupLayout();
    }
    
    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addGroup(
                        layout.createSequentialGroup()
                            .addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(messageScroll, GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(messageScroll, 60, 60, 60));
    }
    
    
    public String sendMessage() {
    	String currentMessage = message.getText().trim();
    	message.setText("");
    	return currentMessage;
    }
    
    public void appendChatLine(String username, String time, String message) {
    	display.append(username + ": " + message + "\n");
    }
    
    //accessors
    public ChatBoxModel getModel() {return model;}
}
