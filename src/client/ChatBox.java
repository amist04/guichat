package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Group;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import emoticons.Emoticon;

import user.User;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants; 
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class ChatBox extends JFrame {

    /**
     * Auto-generated default serial ID.
     */
    private static final long serialVersionUID = 1L;

    private final JTextPane display;
    private final JTextArea message;
    private final JScrollPane displayScroll;
    private final JScrollPane messageScroll;
    private final ChatBoxModel model;
    private final JPanel background;
    private final JPanel gap;
    private final JLabel bottomLabel;
    private Set<User> others;
    private Set<User> leftChat;

    public ChatBox(ChatClientModel chatClientModel, int conversationID,
            String title, boolean isGroupChat) {
        this.model = new ChatBoxModel(chatClientModel, this, conversationID,
                isGroupChat);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("dispose was called");
                model.quitChatBox();
                dispose();
            }
        });
        this.setSize(300, 300);
        this.setTitle(title);

        display = new JTextPane();
        display.setEditable(false);
        displayScroll = new JScrollPane(display);

        message = new JTextArea();
        message.setLineWrap(true);
        messageScroll = new JScrollPane(message);
        message.addKeyListener(model);

        others = new HashSet<User>();
        leftChat = new HashSet<User>();

        background = new JPanel();
        gap = new JPanel();
        gap.setBackground(new Color(0, 51, 102));

        background.setBackground(new Color(0, 51, 102));

        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border textBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border lineBorder = BorderFactory
                .createBevelBorder(BevelBorder.LOWERED);

        display.setBorder(textBorder);
        displayScroll.setBorder(lineBorder);
        message.setBorder(textBorder);
        messageScroll.setBorder(lineBorder);
        background.setBorder(paddingBorder);

        bottomLabel = new JLabel("");
        bottomLabel.setForeground(Color.WHITE);

        this.add(background);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        createGroupLayout();
    }

    private void createGroupLayout() {
        GroupLayout layout = new GroupLayout(background);
        background.setLayout(layout);

        Group h = layout.createParallelGroup();
        h.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(gap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE);
        h.addComponent(messageScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h.addComponent(gap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE);
        h.addComponent(bottomLabel, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        Group v = layout.createSequentialGroup();
        v.addComponent(displayScroll, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v.addComponent(gap, 10, 10, 10);
        v.addComponent(messageScroll, 60, 60, 60);
        v.addComponent(gap, 10, 10, 10);
        v.addComponent(bottomLabel, 20, 20, 20);

        layout.setHorizontalGroup(h);
        layout.setVerticalGroup(v);
    }

    public String sendMessage() {
        String currentMessage = message.getText().trim();
        message.setText("");
        return currentMessage;
    }

    public void appendChatLine(String username, String message) {
        if (!this.isVisible()) {
            this.setVisible(true);
        }
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String text = username + " [" + timestamp + "]:";

        StyledDocument doc = display.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLUE);
        StyleConstants.setBold(keyWord, true);
        
        try {
            doc.insertString(doc.getLength(), text, keyWord);
            doc.insertString(doc.getLength(), " ", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        // add message
        while (message.length() > 0) {
            // see if we have any smileys
            boolean found = false;
            for (int i = 1; i <= message.length(); i++) {
                String substring = message.substring(0, i);
                if (Emoticon.isValid(substring)) {
                    Emoticon emoticon = new Emoticon(substring);
                    ImageIcon icon = new ImageIcon("icons/" + emoticon.getURL());

                    StyleContext context = new StyleContext();

                    Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);

                    JLabel label = new JLabel(icon);
                    StyleConstants.setComponent(labelStyle, label);
                    
                    try {
                        doc.insertString(doc.getLength(), substring, labelStyle);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    
                    message = message.substring(i);
                    found = true;
                    System.out.println("FOUND AN EMOTICON");
                    break;
                }
            }
            int dollarIndex = message.indexOf("\\$");
            if (dollarIndex == 0 && !found) {
            	int endIndex = message.indexOf("$",dollarIndex+2);
            	if (endIndex > -1) {
            		try {
            			String latex = message.substring(dollarIndex+2,endIndex);
            			System.out.println(latex);
            			TeXFormula formula = new TeXFormula(latex);
            			TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(20).build();

            			icon.setInsets(new Insets(5, 5, 5, 5));

            			BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);


            			Graphics2D g2 = image.createGraphics();
            			g2.setColor(Color.white);
            			g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
            			JLabel jl = new JLabel();
            			jl.setForeground(new Color(0, 0, 0));
            			icon.paintIcon(jl, g2, 0, 0);



            			ImageIcon icon1 = new ImageIcon(image);

            			StyleContext context = new StyleContext();

            			Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);

            			JLabel label = new JLabel(icon1);
            			StyleConstants.setComponent(labelStyle, label);

            			try {
            				doc.insertString(doc.getLength(), latex, labelStyle);
            			} catch (BadLocationException e) {
            				e.printStackTrace();
            			}
                        
                        message = message.substring(endIndex + 1);
                        found = true;
                        System.out.println("FOUND A LATEX!");
            		}
            		catch (org.scilab.forge.jlatexmath.ParseException e) {
            			System.out.println("oops! bad latex!");
            		}
            	}
            }
            
            if (!found) {
                try {
                    doc.insertString(doc.getLength(), message.substring(0, 1), null);
                    message = message.substring(1);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("remaining message: " + message);
        
        try {
            doc.insertString(doc.getLength(), "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendMessage(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String text = message + " [" + timestamp + "]\n";
        
        StyledDocument doc = display.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.MAGENTA);
        StyleConstants.setItalic(keyWord, true);
        
        try {
            doc.insertString(doc.getLength(), text, keyWord);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void setBottomMessage(String message) {
        bottomLabel.setText(message);
    }

    private String generateUserListWithVerb(Set<String> users, String verb1,
            String verb2) {
        String res = "";
        if (users.size() == 0) {
            return res;
        } else if (users.size() == 1) {
            Iterator<String> iterator = users.iterator();
            res += iterator.next();
            return res + " " + verb1;
        } else if (users.size() == 2) {
            Iterator<String> iterator = users.iterator();
            res += iterator.next() + " " + iterator.next();
            return res + " " + verb2;
        } else {
            Iterator<String> iterator = users.iterator();
            int num = 0;
            while (num < users.size() - 1) {
                res += iterator.next() + ", ";
            }
            res += "and " + iterator.next();
            return res + " " + verb2;
        }
    }

    public void updateStatus(Set<String> usersTyping,
            Set<String> usersEnteredText) {
        if (usersTyping.size() > 0) {
            this.setBottomMessage(this.generateUserListWithVerb(usersTyping,
                    "is typing...", "are typing..."));
        } else if (usersEnteredText.size() > 0) {
            this.setBottomMessage(this.generateUserListWithVerb(
                    usersEnteredText, "has entered text...",
                    "have entered text..."));
        } else {
            this.setBottomMessage("");
        }
    }

    // accessors
    public ChatBoxModel getModel() {
        return model;
    }

    public JTextArea getMessage() {
        return this.message;
    }

    public JTextPane getDisplay() {
        return this.display;
    }

    public Set<User> getOthers() {
        return others;
    }
    
    public Set<User> getLeftChat() {
    	return leftChat;
    }

    public void addOther(User other) {
    	others.add(other);
    	updateTitle();
    }
    
    public void removeOther(User other) {
    	if (others.contains(other)) {
    		others.remove(other);
    		leftChat.add(other);
    		updateTitle();
    	}
    }
    
    public void updateTitle() {
    	if (others.size() > 0) {
    		String title = "Group chat including ";
        	for (User friend: others) {
        		title += friend.getUsername() + ", ";
        	}
        	this.setTitle(title.substring(0,title.length()-2));
    	}
    	else {
    		this.setTitle("Empty group chat.");
    	}
    }
}
