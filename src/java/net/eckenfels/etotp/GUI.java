/*
 * GUI.java - et-otp: GPL Java TOTP soft token by Bernd Eckenfels.
 */
package net.eckenfels.etotp;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import net.eckenfels.etotp.Base32.DecodingException;


public class GUI implements ActionListener
{
	private static final String VERSION = "1.0";
    private static final String HELPURL = "http://ecki.github.com/et-otp/";
    private static final String PROGNAME = "et-OTP";

	private JPasswordField passwordField;
	private JTextField textField;
	private JLabel textLabel;
	private JFrame frame;
	private JDialog settingsDialog;
	private JDialog aboutDialog;
	private JTextField settingsCode;
	private JPasswordField settingsPass;
	private File configFile = new File(".et-otp.properties");
	private JLabel settingsFileLabel;

    private JLabel statusLabel;


    GUI()
	{
	}


	static void buildMainFrame()
	{
        GridBagLayout layout = new GridBagLayout();
		GUI gui= new GUI();
		gui.frame = new JFrame(PROGNAME);
		gui.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.frame.setLayout(layout);

		JMenuBar mb = new JMenuBar();
		JMenu m = new JMenu(PROGNAME); m.setMnemonic(KeyEvent.VK_E);
		JMenuItem mi = new JMenuItem("Settings"); mi.addActionListener(gui);  m.add(mi);
		mi = new JMenuItem("Quit"); mi.addActionListener(gui);  m.add(mi);
		mb.add(m);
		m = new JMenu("Help"); m.setMnemonic(KeyEvent.VK_H);
		mi = new JMenuItem("Manual"); mi.addActionListener(gui);  m.add(mi);
		mi = new JMenuItem("About"); mi.addActionListener(gui);  m.add(mi);
		mb.add(m);
		gui.frame.setJMenuBar(mb);

        GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.weighty = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 20, 10);
		c.anchor = GridBagConstraints.NORTH;
		JLabel label = new JLabel(PROGNAME + " Soft Token", JLabel.CENTER);
		label.setFont(new Font("Serif", Font.BOLD, 16));
		gui.frame.add(label, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weighty = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0,10,5,10);
		label = new JLabel("Unlock Password:");
		gui.frame.add(label, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weighty = 0;
		c.weightx = 1;
		c.insets = new Insets(0,10,10,10);
		JPasswordField pwField = new JPasswordField("");
        pwField.addActionListener(gui);
        pwField.setFocusable(true);
        gui.frame.add(pwField, c);
        gui.passwordField = pwField;

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 2;
        c.gridheight = 2;
        c.gridwidth = 1;
        c.weighty = 0;
        c.weightx = 0;
        c.ipadx = 0;
        c.insets = new Insets(0,10,10,10);
        c.anchor = GridBagConstraints.CENTER;
        JButton button = new JButton("Calc");
        button.addActionListener(gui);
        gui.frame.add(button, c);

        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
        c.ipadx = 70;
		c.insets = new Insets(0,10,10,10);
		c.anchor = GridBagConstraints.CENTER;
		JTextField text  = new JTextField("");
		text.setFont(new Font("Dialog", Font.BOLD, 16));
		// text.setFocusable(false);
		text.setEditable(false);
		gui.frame.add(text, c);
        gui.textField = text;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 2;
		c.weighty = 0;
		c.insets = new Insets(0,20,10,0);
		c.anchor = GridBagConstraints.CENTER;
		label = new JLabel("Your Code:", JLabel.RIGHT);
		gui.frame.add(label, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 2;
		c.weighty = 0;
		c.anchor = GridBagConstraints.SOUTH;
		c.insets = new Insets(0,20,10,0);
		label = new JLabel("Next Code:", JLabel.RIGHT);
		gui.frame.add(label, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.SOUTH;
		c.insets = new Insets(0,10,10,0);
		label = new JLabel("", JLabel.LEFT);
		gui.frame.add(label, c);
        gui.textLabel = label;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 6;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new Insets(5,5,5,5);
        label = new JLabel("", JLabel.LEFT);
        gui.frame.add(label, c);
        gui.statusLabel = label;

        // now we realize the components
        gui.frame.pack();
		// when this window opens, we want the focus on the password field
        gui.passwordField.requestFocusInWindow();
        // now we can display it
        gui.frame.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("Event: " + e.getActionCommand() + " " + e.getSource() + " " + e.getID());

        statusLabel.setText(" ");
        statusLabel.setForeground(Color.BLACK);

		if ("Quit".equals(e.getActionCommand()))
		{
			System.exit(0);
		}
		else if ("Manual".equals(e.getActionCommand()))
		{
			try {
			    statusLabel.setText("Opening " + HELPURL + " in browser.");
				Desktop.getDesktop().browse(new URI(HELPURL));
			} catch (Exception ignored) { }
		}
		else if ("Save".equals(e.getActionCommand()))
		{
			settingsDialog.setVisible(false);

			String code = settingsCode.getText();
			char[] pass = settingsPass.getPassword();

			try
			{
				writeSecret(code, pass);
			}
			catch (Exception ex)
			{
			    JOptionPane.showMessageDialog(frame, "Error while writing configuration\n" + ex.getClass().getName() + "\n" + ex.getMessage(), PROGNAME + ": Cannot save configuration", JOptionPane.ERROR_MESSAGE);
			}
/*			catch (DecodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (NoSuchAlgorithmException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			} catch (InvalidKeySpecException e6) {
				// TODO Auto-generated catch block
				e6.printStackTrace();
			} catch (NoSuchPaddingException e7) {
				// TODO Auto-generated catch block
				e7.printStackTrace();
			} catch (InvalidKeyException e8) {
				// TODO Auto-generated catch block
				e8.printStackTrace();
			} catch (IllegalBlockSizeException e9) {
				// TODO Auto-generated catch block
				e9.printStackTrace();
			} catch (BadPaddingException e10) {
				// TODO Auto-generated catch block
				e10.printStackTrace();
			}*/
		}
		else if ("About".equals(e.getActionCommand()))
		{
			if (aboutDialog == null) {
				JDialog d = new JDialog(frame,"About " + PROGNAME, true);
				JTextPane t = new JTextPane();
				t.setContentType("text/html");
				t.setText("<h1>eckes' TOTP Generator (" + PROGNAME + ")</h1><p>This generator can be used to produce a TOTP (RFC 6238) time-based one-time password.</p>" +
						  "<p>This is a so called soft-token, it will be initialized with a specified BASE32 secret. This method is compatible with Amazon's MFA for AWS.</p>" +
						  "<p>This Java Program is from <b>Bernd Eckenfels</b>, it includes some code from the reference implementation if HOTP (RFC 4226) as well as Google's BASE32 implementation from the Google Authenticator project.</p>" +
						  "<p>License: GPLv2.<br/>Version: " + VERSION + "<br/>Homepage: <b>" + HELPURL + "</b></p><p>Config file: <b>" + configFile.getAbsolutePath()+"</b></p>");
				t.validate();
				d.add(t);
				d.setSize(600, 400);
				aboutDialog = d;
			}
			aboutDialog.setVisible(true);
		}
		else if ("Settings".equals(e.getActionCommand()))
		{
			if (settingsDialog == null) {
				JDialog d = new JDialog(frame, PROGNAME + " settings", true);
				buildSettingsDialog(d);
				settingsDialog = d;
			}
			settingsCode.setText("");
			settingsPass.setText("");
			settingsFileLabel.setText("Config File " + (configFile.isFile()?"(overwrite)":"(missing)"));
			settingsDialog.setVisible(true);
		}
		else if ("Calc".equals(e.getActionCommand()) || (e.getSource() == passwordField))
		{
			try
			{
				char[] pass = passwordField.getPassword();

				byte[] bytes = readSecret(pass);

				long seconds = System.currentTimeMillis() / 1000;
				long t0 = 0l;
				long step = 30l;
				long counter = (seconds - t0) / step;

				String s = RFC4226.generateOTP(bytes, counter, 6, false, -1);
				textField.setText(s);
				textField.requestFocus();
				textField.setCaretPosition(0);
				textField.moveCaretPosition(6);

				s = RFC4226.generateOTP(bytes, counter+1, 6, false, -1);
				textLabel.setText(s);
                statusLabel.setText("");
			}
			catch (BadPaddingException bp)
			{
                errorText("Bad password.");
			}
			catch (InvalidKeySpecException ik)
			{
			    errorText("Password empty or illegal.");
			}
			catch (FileNotFoundException fn)
			{
			    errorText("Configuration File not found.");
			}
			catch (IOException io)
			{
			    errorText("I/O Error while loading key: " + io.getMessage());
			}
			catch (NoSuchAlgorithmException ns)
			{
			    errorText("Crypto Problem: " + ns);
			}
			catch (InvalidKeyException ik)
			{
			    errorText("Crypto Key Problem: " + ik);
			}
			catch (Exception ex)
			{
			    errorText("Problem: " + ex);
			}
		}
	}


	private void errorText(String text)
    {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(text);
    }


    private void writeSecret(String code, char[] pass)
			throws DecodingException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, FileNotFoundException, IOException
	{
		byte[] codeBytes;
		codeBytes = Base32.decode(code);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		rand.nextBytes(salt);
		SecretKey password = f.generateSecret(new PBEKeySpec(pass, salt, 1000, 128));
		// TODO clean pass
		password = new SecretKeySpec(password.getEncoded(), "AES");

		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, password);

		byte[] enc = c.doFinal(codeBytes);

		System.out.println("WRITE salt=" + Base32.encode(salt) + " encrypted=" + Base32.encode(enc));

		Properties p = new Properties();
		p.put("key.1.name" , "test");
		p.put("key.1.salt", Base32.encode(salt));
		p.put("key.1.encoded", Base32.encode(enc));
		OutputStream os = new FileOutputStream(configFile);
		p.store(os , PROGNAME + " " + VERSION + " softtokens");
		os.close();
	}


	private byte[] readSecret(char[] pass)
			throws FileNotFoundException, IOException, DecodingException,
			InvalidKeySpecException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException
	{
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

		byte[] salt;
		SecretKey password;
		Cipher c;
		byte[] enc;
		Properties p;
		InputStream is = new FileInputStream(configFile);
		p = new Properties();
		p.load(is); is.close();

		salt = Base32.decode((String)p.get("key.1.salt"));
		enc = Base32.decode((String)p.get("key.1.encoded"));

		System.out.println("READ salt=" + Base32.encode(salt) + " encrypted=" + Base32.encode(enc));

		password = f.generateSecret(new PBEKeySpec(pass, salt, 1000, 128));
		// TODO: overwrite pass
		password = new SecretKeySpec(password.getEncoded(), "AES");

		System.out.println(" password=" + Base32.encode(password.getEncoded()));

		c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, password);
		byte[] dec = c.doFinal(enc);

		return dec;
	}


	private void buildSettingsDialog(JDialog dia)
	{
		dia.setLayout(new GridBagLayout());
		dia.setSize(640, 300);
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(10, 10, 10, 10);
		dia.add(new JLabel("Code (base32):", JLabel.RIGHT), c);

		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		settingsCode = new JTextField();
		dia.add(settingsCode, c);

		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0;
		dia.add(new JLabel("Password:", JLabel.RIGHT), c);

		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 1;
		settingsPass = new JPasswordField();
		dia.add(settingsPass, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		JButton button = new JButton("Save");
		button.addActionListener(this);
		dia.add(button,c);

		String file = configFile.getAbsolutePath();
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.insets = new Insets(10,10,0,10);
		settingsFileLabel = new JLabel("Config File " + (configFile.isFile()?"(overwrite)":"(missing)"));
		dia.add(settingsFileLabel, c);

		c.gridx = 1;
		c.gridy = 5;
		c.weightx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(0,10,10,10);
		JLabel label = new JLabel(file);
		dia.add(label, c);
	}


	/** Main method - does not honor any arguments (yet). */
	public static void main(String[] args)
    {
        buildMainFrame();
    }

} // end class GUI

