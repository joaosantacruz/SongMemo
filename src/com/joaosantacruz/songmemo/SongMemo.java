/*
* SoundMemo Project
* @author joaosantacruz.com
* Created on 10/Feb/2011, 23:20:14
*/

package com.joaosantacruz.songmemo;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;



public class SongMemo extends Activity {


	private Song SONG;

    public static final String PREFS_FILE_NAME = "myPrefsFile";

	private boolean isPressingSeekBar = false;

	public int numberOfTracks = 4;
	
	public int selectedBg = 0;
	
	public int [] drawableBg ={R.drawable.bg_01, R.drawable.bg_02, R.drawable.bg_03, R.drawable.bg_04, R.drawable.bg_07, R.drawable.bg_08, R.drawable.bg_06, R.drawable.bg_09, R.drawable.bg_10, R.drawable.bg_11, R.drawable.bg_12};

	
	
	public LinearLayout mainLayout;
	public TextView songTitleLabel;

	public TextView[] trackLabel = new TextView[numberOfTracks];
	public int [] 	trackLabelId ={R.id.TrackLabel_01,R.id.TrackLabel_02,R.id.TrackLabel_03,R.id.TrackLabel_04};
	
	public EditText lyricsTextBox;
	public LinearLayout mainBox;
	public String oldLyricsText;

	public Button[] faderBtnUp = new Button[numberOfTracks];
	public int [] 	faderBtnUpId ={R.id.FaderBtnUp_01,R.id.FaderBtnUp_02,R.id.FaderBtnUp_03,R.id.FaderBtnUp_04};

	public TextView[] faderBtnText = new TextView[numberOfTracks];
	public int [] 	faderBtnTextId ={R.id.FaderBtnText_01,R.id.FaderBtnText_02,R.id.FaderBtnText_03,R.id.FaderBtnText_04};

	public Button[] faderBtnDown = new Button[numberOfTracks];
	public int [] 	faderBtnDownId ={R.id.FaderBtnDown_01,R.id.FaderBtnDown_02,R.id.FaderBtnDown_03,R.id.FaderBtnDown_04};


	public SeekBar[] panBar = new SeekBar[numberOfTracks];
	public int [] 	 panBarId ={R.id.PanBar_01,R.id.PanBar_02,R.id.PanBar_03,R.id.PanBar_04};


	public Button[] muteBtnSelect = new Button[numberOfTracks];
	public int [] 	muteBtnSelectId ={R.id.MuteBtnSelect_01,R.id.MuteBtnSelect_02,R.id.MuteBtnSelect_03,R.id.MuteBtnSelect_04};

	public Button[] recBtnSelect = new Button[numberOfTracks];
	public int [] 	recBtnSelectId ={R.id.RecBtnSelect_01,R.id.RecBtnSelect_02,R.id.RecBtnSelect_03,R.id.RecBtnSelect_04};

	
	public SeekBar 	currentPositionBar;

	public Button stopButton;
	public Button recButton;
	public Button playButton;
	public Button lyricsButton;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		SONG = new Song(this);
		super.onCreate(savedInstanceState);
		
		// set fullScreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		
		setContentView(R.layout.main);

		
       // Restore preferences - bg
       SharedPreferences settings = getSharedPreferences(PREFS_FILE_NAME, 0);
       this.selectedBg = settings.getInt("selectedBg_pref", 0);;


		
		
		Log.v("JOAO", "APP START - Display UI");

		mainLayout = (LinearLayout) findViewById(R.id.MainLayout);
		songTitleLabel = (TextView) findViewById(R.id.SongTitleLabel);
		
		/* UI Elements definition */
		for(int i=0;i<numberOfTracks; i++){

			trackLabel[i] = (TextView) findViewById(trackLabelId[i]);

			faderBtnUp[i] = (Button)  findViewById(faderBtnUpId[i]);
			faderBtnText[i] = (TextView) findViewById(faderBtnTextId[i]);
			faderBtnDown[i] = (Button)  findViewById(faderBtnDownId[i]);

			panBar[i] = (SeekBar)  findViewById(panBarId[i]);

			muteBtnSelect[i] = (Button)  findViewById(muteBtnSelectId[i]);
			recBtnSelect[i]  = (Button)  findViewById(recBtnSelectId[i]);

		}


		mainBox   = (LinearLayout) findViewById(R.id.MainBox);
		lyricsTextBox = (EditText) findViewById(R.id.LyricsTextBox);
		
		// Control bar Buttons definition
		stopButton   = (Button) findViewById(R.id.StopBtn);
		recButton    = (Button) findViewById(R.id.RecBtn);
		playButton   = (Button) findViewById(R.id.PlayBtn);
		lyricsButton = (Button) findViewById(R.id.LyricsBtn);
		

		currentPositionBar = (SeekBar)findViewById(R.id.CurrentPositionBar);
		
		updateGUIState();







		/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * 
		 * 	   UI elements Definition
		 * 
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

		for (int i=0; i<trackLabel.length; i++) {
			muteBtnSelect[i].setOnClickListener(new SentinelaOnClick(i, "muteBtnSelect"));
			recBtnSelect[i].setOnClickListener(new SentinelaOnClick(i, "recBtnSelect"));
			faderBtnDown[i].setOnClickListener(new SentinelaOnClick(i, "faderBtnDown"));
			faderBtnUp[i].setOnClickListener(new SentinelaOnClick(i, "faderBtnUp"));
			panBar[i].setOnSeekBarChangeListener(new SentinelaOnClick(i, "panBar"));
			
		}
		//  - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - -




		/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * 
		 * 	   Main controls Events       position / stop  /  record  / play
		 * 
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
		
		//Main Play-Position Control
		currentPositionBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(isPressingSeekBar){
					currentPositionBar.setProgress(SONG.setPlayPosition(currentPositionBar.getProgress()));
				}
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isPressingSeekBar = true;
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isPressingSeekBar = false;
			}

		});




		/**
		 * Manage Back/Stop button events 
		 * @author joaosantacruz.com
		 */
		stopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					stopButton.setSelected(SONG.songStop());
					recButton.setSelected(false);
					playButton.setSelected(false);
					lyricsButton.setSelected(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		
		
		/**
		 * Manage Record button events 
		 * @author joaosantacruz.com
		 */
		recButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					recButton.setSelected(SONG.songRecord());
					if(!SONG.isRecordingSong() && !SONG.isPlayingSong()){
						Toast mToast = Toast.makeText(getApplicationContext(), "No track selected to record!", Toast.LENGTH_SHORT);
				    	mToast.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		
		
		/**
		 * Manage Record button events 
		 * @author joaosantacruz.com
		 */		
		playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				playButton.setSelected(SONG.songPlay(currentPositionBar.getProgress()));
				if(SONG.isPlayingSong() && !SONG.isRecordingSong()){
					new Thread(new Runnable() {
						public void run() {
							dealWithPlay();
						}
					}).start();
				}
				if(!SONG.isPlayingSong() && !SONG.isRecordingSong()){
					Toast mToast = Toast.makeText(getApplicationContext(), "Nothing to play. All tracks are empty!", Toast.LENGTH_SHORT);
			    	mToast.show();
				}	
			}

		});

		

		/**
		 * Manage Lyrics button events 
		 * @author joaosantacruz.com
		 */		
		lyricsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				lyricsTextBox.setText(SONG.getLyricsText());
				
				if(mainBox.getVisibility()==0){
					oldLyricsText = SONG.getLyricsText();
					mainBox.setVisibility(8);
					lyricsTextBox.setVisibility(0);
				}else{
					Log.v("JOAO", "ESCONDE LYRICS    \n\r" + oldLyricsText + "\n\r" + SONG.getLyricsText());
					if( !oldLyricsText.toString().equals(SONG.getLyricsText()) ){
						Toast mToast = Toast.makeText(getApplicationContext(), "Saving lyrics...", Toast.LENGTH_SHORT);
				    	mToast.show();
						SONG.saveLyrics(SONG.getLyricsText());
					}
					mainBox.setVisibility(0);
					lyricsTextBox.setVisibility(8);
				}
				
			}
		});

		//  - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - -



		
		
		lyricsTextBox.addTextChangedListener(new TextWatcher() { 
			public void onTextChanged (CharSequence text, int start, int before, int after) {
				SONG.setLyricsText(lyricsTextBox.getText().toString());
				SONG.saveSettings();
			}
	
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
	
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
		});
		



		
		
		// Register contextMenu elements - - - - - - - -  - -
		for (int i=0; i<trackLabel.length; i++) {
			registerForContextMenu(trackLabel[i]);
			//registerForContextMenu(openMenuBtn);
		}

		registerForContextMenu(songTitleLabel);


	}







	/**
	 * Update/load UI elements 
	 * @author joaosantacruz.com
	 **/
	public void updateGUIState(){

		for(int i=0;i<(trackLabel.length);i++){
			//Log.v("JOAO", "updateGUIState + + + + + + track1-" + i + " - " + SONG.getSongName());
			trackLabel[i].setText(SONG.tracks.get(i).getTrackName());
			muteBtnSelect[i].setSelected(SONG.tracks.get(i).isMuted());
			recBtnSelect[i].setSelected(SONG.tracks.get(i).isRecordable());
			faderBtnText[i].setText(Integer.toString(SONG.tracks.get(i).getLeftVolume()));
			//Log.v("JOAO", "get balance value + + + + + + track-" + i + " - " + SONG.tracks.get(i).getBalance());
			panBar[i].setProgress(SONG.tracks.get(i).getBalance());
		}		
		lyricsTextBox.setText(SONG.getLyricsText());
        
		mainLayout.setBackgroundDrawable(getResources().getDrawable(drawableBg[this.selectedBg]));

		SONG.saveSettings();
	}


	
	
	


	/*
	* This method deals with play action - Current position bar - control
	* @author joaosantacruz.com
	*/
	public void dealWithPlay(){

		int maxPosition = SONG.maxTrackDuration("track_duration");

		int curPosition = SONG.tracks.get(SONG.maxTrackDuration("track_number")).mediaPlayer.getCurrentPosition();
		SeekBar currentPositionBar = (SeekBar)findViewById(R.id.CurrentPositionBar);
		SONG.setPlayPosition(currentPositionBar.getProgress());
		while((curPosition+20)<maxPosition && maxPosition>0 ){

			if(!isPressingSeekBar){
				//Log.v("JOAO", "SONG BAR PROCESS - - Total:" + curPosition + " | | | " + maxPosition + " ________  " + (curPosition*100/maxPosition) + " ppppp " + SONG.maxTrackDuration("track_number"));
				curPosition = SONG.tracks.get(SONG.maxTrackDuration("track_number")).mediaPlayer.getCurrentPosition();
				currentPositionBar.setProgress((int)((curPosition*100/maxPosition)));

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

			if(!SONG.tracks.get(SONG.maxTrackDuration("track_number")).mediaPlayer.isPlaying()) break;

		}
		
		SONG.songStop();
		currentPositionBar.setProgress(0);
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
		
	}

	/*
	* This handler deals with end-of-play
	* @author joaosantacruz.com
	*/
	protected final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			playButton.setSelected(false);	        }
	};

	// End -  Deal with play action -- - - - - - - - - - - - - - - -



















	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * 
	 * 	   	Context Menu . Track options - Used on track's label long press
	 * 		@author joaosantacruz.com
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		
		
		// Change App Background -- - - - -- - - - - - - - - - 
		if(v.getId()==R.id.SongTitleLabel){
									
				if((selectedBg+1) >= drawableBg.length){
					this.selectedBg = 0;
				}else{
					this.selectedBg ++;
				}
				mainLayout.setBackgroundDrawable(getResources().getDrawable(drawableBg[selectedBg]));

				
		      // We need an Editor object to make preference changes.
		      // All objects are from android.context.Context
		      SharedPreferences settings = getSharedPreferences(PREFS_FILE_NAME, 0);
		      SharedPreferences.Editor editor = settings.edit();
		      editor.putInt("selectedBg_pref", this.selectedBg);

		      // Commit the edits!
		      editor.commit();

				
				
		}
		// - - - - - - - - - - - - - - - - - - - - - - - - - - -
		
		
		int trackNumber = -1;

		// EDIT - TRACK n
		for (int i=0; i<trackLabel.length; i++) {
			if(v.getId()==trackLabelId[i]){ 
				menu.setHeaderTitle("Edit  '" + trackLabel[i].getText());
				trackNumber = i;
				break;
			}
		}

		if(trackNumber>=0){
			menu.add(trackNumber, v.getId(), 1, "Rename track");
			menu.add(trackNumber, v.getId(), 2, "EFX");
			menu.add(trackNumber, v.getId(), 3, "Clean");
		}

	}

	
	
	@Override
	public boolean onContextItemSelected(final MenuItem item) {

		switch (item.getOrder()) {

		case 1: // EDIT track - RENAME
			Log.v("JOAO", "CONTEXT MENU - RENAME track + + + + + + ");


			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);

			alert.setView(input);
			alert.setTitle("Insert a new track name:");
			input.requestFocus();

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int optButton) {
					String trackName = input.getText().toString().trim();
					trackLabel[item.getGroupId()].setText(SONG.renameTrack(trackName, item.getGroupId()));		
				}
			});

				alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					dialog.cancel();
				}
			});

			alert.show();
			break;

			

		case 2: // EDIT track - EFX
			Toast mToast = Toast.makeText(getApplicationContext(), "This feature is not yet implemented.", Toast.LENGTH_SHORT);
	    	mToast.show();
			Log.v("JOAO", "CONTEXT MENU - EFX track + + + + + + ");
			break;

			

		case 3: // EDIT track - CLEAN
			SONG.clearTrack(item.getGroupId());
			Log.v("JOAO", "CONTEXT MENU - CLEAN track + + + + + + ");

			break;

		}


		return true;

	}












	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * 
	 * 	   	MAIN menu
	 * 		@author joaosantacruz.com
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.menu, menu);
		

		
		return(super.onPrepareOptionsMenu(menu));
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		
		switch (item.getItemId()) {

		
		/* = = = = = = = = = = = = = = =    MENU OPTION - NEW    = = = = = = = = = = = = = = = = = = = = = = =*/
		case R.id.newmenubtn:

			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);

			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// only will trigger it if no physical keyboard is open
			mgr.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

			alert.setView(input);
			alert.setTitle("Please name your song?");

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					String songName = input.getText().toString().trim();
					final String msg = SONG.newSong(songName);
					TextView songTitleLabel  = (TextView) findViewById(R.id.SongTitleLabel);
					songTitleLabel.setText(songName);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();			
				}
			});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					dialog.cancel();
				}
			});

			alert.show();
			break;

			
			/* = = = = = = = = = = = = = = =    MENU OPTION - OPEN    = = = = = = = = = = = = = = = = = = = = = = =*/
		case R.id.openmenubtn:

			//menu.setHeaderTitle("Open Song");
			File[] listOfSongs = SONG.getSongsList();
			item.getSubMenu().clear();
			for (File song: listOfSongs)
			{
				if (song.isDirectory())
				{
					item.getSubMenu().add(99, 4444, 0, song.getName());
				}
			}
			
			break;

			/* = = = = = = = = = = = = = = =    MENU OPTION - DELETE    = = = = = = = = = = = = = = = = = = = = = = =*/
		case R.id.deletemenubtn:     

			final AlertDialog.Builder alertDelete = new AlertDialog.Builder(this);
			alertDelete.setIcon(android.R.drawable.ic_dialog_alert);

			alertDelete.setTitle("Delete Song!");
			alertDelete.setMessage("Are you shure you want to delete this song and all it's tracks?");

			alertDelete.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					if(SONG.deleteSong()){
						TextView songTitleLabel  = (TextView) findViewById(R.id.SongTitleLabel);
						songTitleLabel.setText(SONG.songName);
						Toast.makeText(getApplicationContext(), "Song is deleted!", Toast.LENGTH_SHORT).show();			
					}
				}
			});

			alertDelete.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					dialog.cancel();
				}
			});
			alertDelete.show();

			break;


			/* = = = = = = = = = = = = =    MENU OPTION - MIX & SHARE    = = = = = = = = = = = = = = = = = = =*/
		case R.id.saveas:     


			final AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
			final EditText input2 = new EditText(this);

			InputMethodManager mgr2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// only will trigger it if no physical keyboard is open
			mgr2.showSoftInput(input2, InputMethodManager.SHOW_IMPLICIT);

			alert2.setView(input2);
			alert2.setTitle("Save song as?");

			alert2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					String songName = input2.getText().toString().trim();
					final String msg = SONG.renameSong(songName);
					TextView songTitleLabel  = (TextView) findViewById(R.id.SongTitleLabel);
					songTitleLabel.setText(songName);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				}
			});

			alert2.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					dialog.cancel();
				}
			});

			alert2.show();
			break;


			/* = = = = = = = = = = = = = = =    MENU OPTION - QUIT    = = = = = = = = = = = = = = = = = = = = = = =*/
		case R.id.quitmenubtn:
			Toast.makeText(getApplicationContext(), "Saving SongMemo's settings...\nSee you nex time!", Toast.LENGTH_SHORT).show();
			terminate();
			break;



			/* = = = = = = = = = = = = = = =    MENU OPTION - ABOUT    = = = = = = = = = = = = = = = = = = = = = = =*/
		case R.id.aboutmenubtn:     
			final AlertDialog.Builder alertAbout = new AlertDialog.Builder(this);
			alertAbout.setTitle("SongMemo's message");
			alertAbout.setIcon(android.R.drawable.ic_menu_info_details);
			alertAbout.setMessage("As musician and software developer, I've comited myself to build a multitrack audio recorder (4 track like) for the Android platform.\n\nOpenness bring us fast access to knowledge and, as consequence, better solutions. That is why SongMemo is a Free and Living Open Source project.\n\nContributors are welcome!\n\nIf you have ideas that you wish to see implemented or if you somehow want to be a part of songMemo's developer team, feel free to contact me.\n\nFurther info, please visit:\nwww.joasantacruz.com/songmemo");
			alertAbout.setNegativeButton("Close", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int optButton) {
					dialog.cancel();
				}
			});
			alertAbout.show();
			break;
			
		}
		
		// if this is submenu item - - - - - -
		if(item.getItemId()==4444){
			TextView songTitleLabel  = (TextView) findViewById(R.id.SongTitleLabel);
			songTitleLabel.setText(SONG.openSong((String) item.getTitle())); 
			updateGUIState();
		}

		return (super.onOptionsItemSelected(item));

	}




	public void terminate()
	{
		Log.i("myid","terminated!!");
		super.onDestroy();
		this.finish();
	}

	public void onTerminate(){
		terminate();
	}


	
	
	
	
	
	
	class SentinelaOnClick implements View.OnClickListener, OnSeekBarChangeListener {
		private int i;
		private String element = "";
		private Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		
		public SentinelaOnClick(int i, String element) {
			super();
			this.i = i;
			this.element = element;
		}
	
		public void onClick(View v) {
			Log.v("JOAO", "CLICK - - - -  " + element + " - - track-" + i);
			
			if(element=="muteBtnSelect"){
				muteBtnSelect[i].setSelected(SONG.toggleMute(i));
				vib.vibrate(25);
			}
			
			if(element=="recBtnSelect"){
				recBtnSelect[i].setSelected(SONG.toggleRecordable(i));
				vib.vibrate(25);
			}
			
			if(element=="faderBtnUp"){
				faderBtnText[i].setText(Integer.toString(SONG.volumeUp(i)));
			}
			if(element=="faderBtnDown"){
				faderBtnText[i].setText(Integer.toString(SONG.volumeDown(i)));
			}
			
			updateGUIState();
		}

	
	    public void onLongClick(View v) {
	    	Log.v("JOAO", "SOOOOOOOOOOOLLLLLOOOOOOOOOOOOO track" + i);
			if(element=="muteBtnSelect"){
				muteBtnSelect[i].setSelected(SONG.soloTrack(i));
				updateGUIState();
			}			
		}
		
		
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			Log.v("JOAO", "SET Balance " + i + " - " + panBar[i].getProgress());
			SONG.setBalance(i, panBar[i].getProgress());
			updateGUIState();
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}		
		
		
	}


}
