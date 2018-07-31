package com.bettercolors.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.bettercolors.main.Bettercolors;

public class InformationWindow extends Window{

	private JTextPane info_box;
	private Bettercolors bettercolors;
	private LinkedHashMap<JLabel, JSlider> sliders;
	private ArrayList<JCheckBox> checkboxes;
	private JLabel status;

	
	public InformationWindow(String title, int width, int height, String text, Bettercolors bettercolors) {
		super(title, width, height);
		this.bettercolors = bettercolors;
		
		setupSettingsComponents();
		setupConsole(text);
		setupBottomButtons();
		
		pack();
		super.update();
	}

	
	public void addText(String text){
		addText(text,Color.WHITE,false);
	}
	
	public void addText(String text, boolean new_line){
		addText(text,Color.WHITE,new_line);
	}
	
	public void addText(String text, Color color, boolean new_line){
		
		if(new_line){
			appendToPane(info_box, "\n"+text, color, new_line);
		}else{
			appendToPane(info_box, text, color, new_line);
		}
		
		super.update();
	}
	
	private void appendToPane(JTextPane tp, String msg, Color c, boolean new_line)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        
        try {
			tp.getDocument().insertString(len, msg, aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }
	
	public void resetText(){
		info_box.setText("");
		super.update();
	}
	
	private void setupSettingsComponents(){
		
		// Panel which will contain components
		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(15,2));
		grid.setBorder ( new TitledBorder ( new EtchedBorder (), "Settings" ) );;

		// Components init / custom

		checkboxes = createCheckBoxes();
		sliders = createSlidersAndLabels();
		
		for(JCheckBox cb : checkboxes){
			grid.add(cb);
		}

		for(Entry<JLabel, JSlider> entry : sliders.entrySet()){
			grid.add(entry.getKey());
			grid.add(entry.getValue());
		}
		getContentPane().add(grid, "North");
	}
	

	private void setupConsole(String text){
		
		// Panel which will contain components
		JPanel middlePanel = new JPanel ();
		middlePanel.setBorder ( new TitledBorder ( new EtchedBorder (), "Console" ) );
		middlePanel.setLayout(new GridLayout(1,1));
		
		// TextArea & ScrollPane init
		//info_box = new JTextArea(text, 16, 58);
		info_box = new JTextPane();
		//info_box.setPreferredSize(new Dimension(350,250));
		JScrollPane scroll = new JScrollPane (info_box);
		scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(425,250));
		
		// TextArea custom
		//info_box.setWrapStyleWord(true);
		info_box.setEditable(false);
		info_box.setFont(new Font("Lucida Console", Font.PLAIN, 14));
		info_box.setBackground(new Color(0,30,50));
		info_box.setForeground(Color.WHITE);
		info_box.setText(text);

		// Put the panel on the window
		middlePanel.add(scroll);
	    getContentPane().add(middlePanel,"Center");
	}
	
	private void setupBottomButtons(){
		// Panel which will contain components
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		
		// Button init / custom
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.saveSettings();
			}
		});
		
		JButton load = new JButton("Load");
		load.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bettercolors.getSettings();
			}	
		});
		
		JButton check = new JButton("Check");
		check.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bettercolors.sendReport();
			}
		});
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				resetText();
			}
		});
		
		status = new JLabel(" Module status ");
		status.setBorder(BorderFactory.createLineBorder(Color.black));
		//status.setFont(new Font("Arial",Font.PLAIN,18));
		status.setForeground(Color.WHITE);
		status.setOpaque(true);
		setPlayerAttacking(false);
		
		
		// Put the panel on the window
		getContentPane().add(buttonsPanel, "South");
		buttonsPanel.add(save);
		buttonsPanel.add(load);
		buttonsPanel.add(check);
		buttonsPanel.add(clear);
		buttonsPanel.add(status);
	}

	public void setPlayerAttacking(boolean attacking){
		if(attacking){
			status.setBackground(Color.GREEN);
		}else{
			status.setBackground(Color.RED);
		}
		repaint();
	}
	
	private ArrayList<JCheckBox> createCheckBoxes() {

		ArrayList<JCheckBox> buttons = new ArrayList<JCheckBox>();

		final JCheckBox aimassist = new JCheckBox("Toggle AimAssist");
		//updateButtonName(aimassist, "Toggle AimAssist", bettercolors.isAimActivated());
		aimassist.setSelected(bettercolors.isAimActivated());
		aimassist.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.setAimActivated(!bettercolors.isAimActivated());
				aimassist.setSelected(bettercolors.isAimActivated());
				repaint();
			}
		});
		buttons.add(aimassist);

		final JCheckBox clickassist = new JCheckBox("Toggle ClickAssist");
		clickassist.setSelected( bettercolors.isClickAssistActivated());
		clickassist.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.setClickAssistActivated(!bettercolors.isClickAssistActivated());
				clickassist.setSelected(bettercolors.isClickAssistActivated());
				repaint();
			}
		});
		buttons.add(clickassist);
		
		final JCheckBox stopaim = new JCheckBox("Toggle StopAimOnRightClick");
		stopaim.setSelected( bettercolors.isAim_stop_def());
		stopaim.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.setAim_stop_def(!bettercolors.isAim_stop_def());
				stopaim.setSelected(bettercolors.isAim_stop_def());
				repaint();
			}
		});
		buttons.add(stopaim);
		
		final JCheckBox cpsbypass = new JCheckBox("Toggle CPSBypass (packets)");
		cpsbypass.setSelected( bettercolors.isCps_bypass());
		cpsbypass.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.setCps_bypass(!bettercolors.isCps_bypass());
				cpsbypass.setSelected(bettercolors.isCps_bypass());
				repaint();
			}
		});
		buttons.add(cpsbypass);
		
		final JCheckBox cpsonlyentity = new JCheckBox("Toggle CPSOnlyOnEntity");
		cpsonlyentity.setSelected(bettercolors.isCps_only_on_entity());
		cpsonlyentity.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.setCps_only_on_entity(!bettercolors.isCps_only_on_entity());
				cpsonlyentity.setSelected(bettercolors.isCps_only_on_entity());
				repaint();
			}
		});
		buttons.add(cpsonlyentity);
		
		final JCheckBox useonmobs = new JCheckBox("Toggle UseOnMobs");
		useonmobs.setSelected(bettercolors.isUse_on_mobs());
		useonmobs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.setUse_on_mobs(!bettercolors.isUse_on_mobs());
				useonmobs.setSelected(bettercolors.isUse_on_mobs());
				repaint();
			}
		});
		buttons.add(useonmobs);
		
		final JCheckBox teamfilter = new JCheckBox("Toggle TeamFilter");
		teamfilter.setSelected(bettercolors.isTeam_filter());
		teamfilter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bettercolors.setTeam_filter(!bettercolors.isTeam_filter());
				teamfilter.setSelected(bettercolors.isTeam_filter());
				repaint();
			}
		});
		buttons.add(teamfilter);
		
		final JCheckBox empty = new JCheckBox();
		empty.setVisible(false);
		buttons.add(empty);

		return buttons;
	}
	
	private LinkedHashMap<JLabel, JSlider> createSlidersAndLabels(){
		LinkedHashMap<JLabel, JSlider> sliders = new LinkedHashMap<JLabel, JSlider>();
		 
		 final JLabel aimrefreshratelabel = new JLabel("AimRefreshRate(x/sec) [" + bettercolors.getAim_refreshrate() + "]");
		 final JSlider aimrefreshrateslider = new JSlider();
		 aimrefreshrateslider.setMinimum(0);
		 aimrefreshrateslider.setMaximum(1000);
		 aimrefreshrateslider.setValue((int) bettercolors.getAim_refreshrate());
		 aimrefreshrateslider.setMajorTickSpacing(200);
		 aimrefreshrateslider.setMinorTickSpacing(50);
		 aimrefreshrateslider.setPaintTicks(true);
		 aimrefreshrateslider.addChangeListener(new ChangeListener(){
			 @Override
			 public void stateChanged(ChangeEvent e) {
				 bettercolors.setAim_refreshrate(aimrefreshrateslider.getValue());
				 aimrefreshratelabel.setText("AimRefreshRate(x/sec) [" + bettercolors.getAim_refreshrate() + "]");
				 repaint();
			 }
		 });
		 sliders.put(aimrefreshratelabel, aimrefreshrateslider);
		
		final JLabel aimstepxlabel = new JLabel("AimStepX [" + bettercolors.getAim_step_X() + "]");
		final JSlider aimsetpxslider = new JSlider();
		aimsetpxslider.setMinimum(0);
		aimsetpxslider.setMaximum(20);
		aimsetpxslider.setValue((int)bettercolors.getAim_step_X());
		aimsetpxslider.setMajorTickSpacing(5);
		aimsetpxslider.setMinorTickSpacing(1);
		aimsetpxslider.setPaintTicks(true);
		aimsetpxslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setAim_step_X(aimsetpxslider.getValue());
				aimstepxlabel.setText("AimStepX [" + bettercolors.getAim_step_X() + "]");
			}
		});
	    sliders.put(aimstepxlabel, aimsetpxslider);

		final JLabel aimstepylabel = new JLabel("AimStepY [" + bettercolors.getAim_step_Y() + "]");
		final JSlider aimsetpyslider = new JSlider();
		aimsetpyslider.setMinimum(0);
		aimsetpyslider.setMaximum(20);
		aimsetpyslider.setValue((int)bettercolors.getAim_step_Y());
		aimsetpyslider.setMajorTickSpacing(5);
		aimsetpyslider.setMinorTickSpacing(1);
		aimsetpyslider.setPaintTicks(true);
		aimsetpyslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setAim_step_Y(aimsetpyslider.getValue());
				aimstepylabel.setText("AimStepY [" + bettercolors.getAim_step_Y() + "]");
				repaint();
			}
		});
		sliders.put(aimstepylabel, aimsetpyslider);
		
		final JLabel aimrangelabel = new JLabel("AimRange [" + bettercolors.getAim_range() + "]");
		final JSlider aimrangeslider = new JSlider();
		aimrangeslider.setMinimum(0);
		aimrangeslider.setMaximum(10);
		aimrangeslider.setValue((int)bettercolors.getAim_range());
		aimrangeslider.setMajorTickSpacing(5);
		aimrangeslider.setMinorTickSpacing(1);
		aimrangeslider.setPaintTicks(true);
		aimrangeslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setAim_range(aimrangeslider.getValue());
				aimrangelabel.setText("AimRange [" + bettercolors.getAim_range() + "]");
				repaint();
			}
		});
		sliders.put(aimrangelabel, aimrangeslider);
		
		final JLabel aimradiusxlabel = new JLabel("AimRadiusX [" + bettercolors.getAim_radius_X() + "]");
		final JSlider aimradiusxslider = new JSlider();
		aimradiusxslider.setMinimum(0);
		aimradiusxslider.setMaximum(180);
		aimradiusxslider.setValue((int)bettercolors.getAim_radius_X());
		aimradiusxslider.setMajorTickSpacing(20);
		aimradiusxslider.setMinorTickSpacing(5);
		aimradiusxslider.setPaintTicks(true);
		aimradiusxslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setAim_radius_X(aimradiusxslider.getValue());
				aimradiusxlabel.setText("AimRadiusX [" + bettercolors.getAim_radius_X() + "]");
				repaint();
			}
		});
		sliders.put(aimradiusxlabel, aimradiusxslider);
		
		final JLabel aimradiusylabel = new JLabel("AimRadiusY [" + bettercolors.getAim_radius_Y() + "]");
		final JSlider aimradiusyslider = new JSlider();
		aimradiusyslider.setMinimum(0);
		aimradiusyslider.setMaximum(90);
		aimradiusyslider.setValue((int)bettercolors.getAim_radius_Y());
		aimradiusyslider.setMajorTickSpacing(10);
		aimradiusyslider.setMinorTickSpacing(5);
		aimradiusyslider.setPaintTicks(true);
		aimradiusyslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setAim_radius_Y(aimradiusyslider.getValue());
				aimradiusylabel.setText("AimRadiusY [" + bettercolors.getAim_radius_Y() + "]");
				repaint();
			}
		});
		sliders.put(aimradiusylabel, aimradiusyslider);

		final JLabel cpsincrementlabel = new JLabel("CPSIncrement [" + bettercolors.getCps_increment() + "]");
		final JSlider cpsincrementslider = new JSlider();
		cpsincrementslider.setMinimum(0);
		cpsincrementslider.setMaximum(5);
		cpsincrementslider.setValue((int) bettercolors.getCps_increment());
		cpsincrementslider.setMajorTickSpacing(1);
		cpsincrementslider.setPaintTicks(true);
		cpsincrementslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setCps_increment(cpsincrementslider.getValue());
				cpsincrementlabel.setText("CPSIncrement [" + bettercolors.getCps_increment() + "]");
				repaint();
			}
		});
		sliders.put(cpsincrementlabel, cpsincrementslider);
		
		final JLabel cpschancelabel = new JLabel("CPSChance [" + bettercolors.getCps_chance() + "]");
		final JSlider cpschanceslider = new JSlider();
		cpschanceslider.setMinimum(0);
		cpschanceslider.setMaximum(100);
		cpschanceslider.setValue((int) bettercolors.getCps_chance());
		cpschanceslider.setMajorTickSpacing(20);
		cpschanceslider.setMinorTickSpacing(5);
		cpschanceslider.setPaintTicks(true);
		cpschanceslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setCps_chance(cpschanceslider.getValue());
				cpschancelabel.setText("CPSChance [" + bettercolors.getCps_chance() + "]");
				repaint();
			}
		});
		sliders.put(cpschancelabel, cpschanceslider);
		
		final JLabel activationtimelabel = new JLabel("ActivationTime(ms) [" + bettercolors.getActivation_time() + "]");
		final JSlider activationtimeslider = new JSlider();
		activationtimeslider.setMinimum(0);
		activationtimeslider.setMaximum(10000);
		activationtimeslider.setValue((int) bettercolors.getActivation_time());
		activationtimeslider.setMajorTickSpacing(1000);
		activationtimeslider.setMinorTickSpacing(250);
		activationtimeslider.setPaintTicks(true);
		activationtimeslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setActivation_time(activationtimeslider.getValue());
				activationtimelabel.setText("ActivationTime(ms) [" + bettercolors.getActivation_time() + "]");
				repaint();
			}
		});
		sliders.put(activationtimelabel, activationtimeslider);
		
		final JLabel clickstoactivatelabel = new JLabel("ClicksToActivate [" + bettercolors.getAttacks_to_toggle() + "]");
		final JSlider clickstoactivateslider = new JSlider();
		clickstoactivateslider.setMinimum(0);
		clickstoactivateslider.setMaximum(20);
		clickstoactivateslider.setValue((int) bettercolors.getAttacks_to_toggle());
		clickstoactivateslider.setMajorTickSpacing(5);
		clickstoactivateslider.setMinorTickSpacing(2);
		clickstoactivateslider.setPaintTicks(true);
		clickstoactivateslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setAttacks_to_toggle(clickstoactivateslider.getValue());
				clickstoactivatelabel.setText("ClicksToActivate [" + bettercolors.getAttacks_to_toggle() + "]");
				repaint();
			}
		});
		sliders.put(clickstoactivatelabel, clickstoactivateslider);
		
		
		final JLabel timetoactivatelabel = new JLabel("TimeToActivate(ms) [" + bettercolors.getTime_to_toggle() + "]");
		final JSlider timetoactivateslider = new JSlider();
		timetoactivateslider.setMinimum(0);
		timetoactivateslider.setMaximum(10000);
		timetoactivateslider.setValue((int) bettercolors.getTime_to_toggle());
		timetoactivateslider.setMajorTickSpacing(1000);
		timetoactivateslider.setMinorTickSpacing(250);
		timetoactivateslider.setPaintTicks(true);
		timetoactivateslider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				bettercolors.setTime_to_toggle(timetoactivateslider.getValue());
				timetoactivatelabel.setText("TimeToActivate(ms) [" + bettercolors.getTime_to_toggle() + "]");
				repaint();
			}
		});
		sliders.put(timetoactivatelabel, timetoactivateslider);
		
		
		return sliders;
	}
	
	public void updateComponents(){
		for(Map.Entry<JLabel, JSlider> entry : sliders.entrySet()){
			
			// On fire un event de changement qui va update les sliders (on set un slider � sa valeur actuelle)
			//entry.getValue().setValue(entry.getValue().getValue());
			
			 ChangeEvent ce = new ChangeEvent(entry.getValue());
			 for(ChangeListener cl : entry.getValue().getChangeListeners()){
				 cl.stateChanged(ce);
			 }
		}
		
		for(JCheckBox checkbox : checkboxes){
			
			if(checkbox.getText().equalsIgnoreCase("Toggle AimAssist")){
				checkbox.setSelected(bettercolors.isAimActivated());
			}else if(checkbox.getText().equalsIgnoreCase("Toggle ClickAssist")){
				checkbox.setSelected(bettercolors.isClickAssistActivated());
			}
			repaint();
		    
		}
		
	}

	public void toggle() {
		setVisible(!isVisible());
	}
}

