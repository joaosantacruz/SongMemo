/*
* SoundMemo Project
* @author joaosantacruz.com
* Created on 10/Feb/2011, 23:20:14
*/

package com.joaosantacruz.songmemo;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.util.Log;

public class Track {


	static final String APPDIR = "/sdcard/SongMemo/";
	static final String PREFIX = "track_";
	static final String EXTENSION = ".3gpp";

	private boolean isRecording = false;
	private boolean isPlaying = false;
	private boolean isMuted = false;
	private boolean isRecordable = false;
	private boolean isRecorded = false;

	private int leftVolume = 9;
	private int rightVolume = 9;
	private int balance = 50;

	private int playCurrentPosition = 0;


	private String trackName = "";
	private String trackPath = "";
	private String trackNumber = "";
	private String songName = "";
	private String lastUpdate = "";


	MediaPlayer mediaPlayer = new MediaPlayer();
	MediaRecorder mediaRecorder = new MediaRecorder();


	
	
	
	
	
	public Track(String songname, String tracknumber) {
		
		try {
			
			// Initializing track properties - - - - - 
			setTrackName(PREFIX + tracknumber);
			setSongName(songname);

			setTrackPath(createTrackFile());
			setTrackNumber(tracknumber);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	
	
	
	
	/*
	* This method starts to record individual track 
	* @author joaosantacruz.com
	*/
	public boolean startRecording() {

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // DEFAULT - MPEG_4 - RAW_AMR - THREE_GPP	
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(trackPath);

		try {
			mediaRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaRecorder.start();
		
		setLastUpdate(getDateTime());
		
		Log.v("JOAO", "TRACK - RECORD Track-" + trackNumber);
		
		isRecording = true;
		
		return isRecording;

	}

	
	
	
	
	
	
	/*
	* This method stops individual track from recording
	* @author joaosantacruz.com
	*/
	boolean stopRecording() {
		Log.v("JOAO", "TRACK - STOP record track "+ trackNumber);
		mediaRecorder.stop();
		this.isRecording = false;
		return isRecording;

	}
	
	
	
	
	
	
	/*
	* This method starts to play individual track 
	* @author joaosantacruz.com
	*/
	public boolean startPlaying() {

	
		mediaPlayer.reset();

		try {
			mediaPlayer.setDataSource(trackPath);
			mediaPlayer.prepare();
		} catch (IOException e) {
			Log.v(("Song MEMO"), e.getMessage());
		}

		mediaPlayer.start();
		
		this.setVolume();
				
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer arg0) {
				//mediaPlayer.pause();
				mediaPlayer.seekTo(0);
			}

		});

		this.isPlaying = true;
		return isPlaying;
	}



	
	
	/*
	* This method stops individual track from playing
	* @author joaosantacruz.com
	*/
	boolean stopPlaying() {
		Log.v("JOAO", "TRACK - STOP play track " + trackNumber);

		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(trackPath);
			mediaPlayer.prepare();
			
		} catch (IOException e) {
			Log.v(("Song MEMO"), e.getMessage());
		}

		this.setPlaying(false);
		return isPlaying;
	}

	
	/*
	* This method pauses individual track 
	* @author joaosantacruz.com
	*/
	boolean pausePlaying() {
		Log.v("JOAO", "TRACK - STOP play track " + trackNumber);
		mediaPlayer.pause();
		this.isPlaying = false;
		return isPlaying;
	}


	
	
	
	
	
	/*
	* This method mutes individual track 
	* @author joaosantacruz.com
	*/
	public void setMuted(boolean isMuted) {
		
		this.isMuted = isMuted;
		
		if (isMuted) {
			mediaPlayer.setVolume(0, 0);
		} else {
			setVolume();
		}
		
		
	}

	

	
	/*
	* This method get date-time using format 'yyyy-MM-dd HH:mm:ss'
	* @author joaosantacruz.com
	*/
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	
	
	
	/*
	* This method prints several vars for debug propose
	* @author joaosantacruz.com
	*/
	public void debug(String msg) {
		Log.v("JOAO", " = = = = =  = = = = =  = = = = =  = = = = =  = = = = = ");
		Log.v("JOAO", msg);
		Log.v("JOAO", "trackNumber - " + trackNumber);
		Log.v("JOAO", "isRecording - " + isRecording);
		Log.v("JOAO", "isPlaying - " + isPlaying);
		Log.v("JOAO", "isMuted - " + isMuted);
		Log.v("JOAO", "isRecordable - " + isRecordable);
		Log.v("JOAO", "isRecorded - " + isRecorded);
		Log.v("JOAO", "leftVolume - " + leftVolume);
		Log.v("JOAO", "rightVolume - " + rightVolume);
		Log.v("JOAO", "trackName - " + trackName);
		Log.v("JOAO", "trackPath - " + trackPath);
		Log.v("JOAO", "trackNumber - " + trackNumber);
		Log.v("JOAO", "songName - " + songName);
		Log.v("JOAO", " = = = = =  = = = = =  = = = = =  = = = = =  = = = = = ");
	}

	
	
	
	
	
	
	
	
	
	/*
	* This method sets individual track volume 
	* @author joaosantacruz.com
	*/
	public void setVolume() {

		
		float trackLeftVolume = 0;
		float trackRightVolume = 0;
		

		if(balance>50){
			trackLeftVolume = (100-balance+1)/10;
		}else{ 
			trackLeftVolume = rightVolume; 
		}
		if(balance<50){
			trackRightVolume = (balance+1)/10;
		} else {
			trackRightVolume = leftVolume;
		}
		
		if(this.isMuted){ 
			trackRightVolume=0;
			trackLeftVolume=0;
		}
		
		Log.v("JOAO", "SET VOLUME | mute:"+this.isMuted+" (TRACK-"+trackNumber+" > > > MAIN[L/R]="+ rightVolume +"/" + leftVolume + " - - FINAL[L/R]=" + trackLeftVolume/10 + "/" + trackRightVolume/10 + ") balance("+balance+")");
		mediaPlayer.setVolume(trackLeftVolume/10, trackRightVolume/10);
		
	}

	
	

	/*
	* This method toogle track button 
	* @author joaosantacruz.com
	*/
	public boolean toggleTrackRec() {

		if (isRecordable()) {
			setRecordable(false);
		} else {
			setRecordable(true);
		}
		return (isRecordable);

	}

	/*
	* This method creates track file 
	* @author joaosantacruz.com
	*/
	public String createTrackFile() throws IOException {

		String filename = trackName + EXTENSION;
		String filepath = APPDIR + songName + "/" + filename;
		//
		File f = new File(filepath);

		if (!f.exists()) {
			try {
				f.createNewFile();
				Log.v("JOAO", "CREATING: " + filepath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (f.length() > 0) {
				
				setRecorded(true);
				
				//Log.v("JOAO", "FILE   S I Z E ::: " + f.length() + " - isRecorded= " + isRecorded);
				
			}
			// TODO Tell the user!

		}

		return (filepath);

		// Log.v("JOAO", "CREATING: " + APPDIR + songName + "/" + filename);

	}

	
	
	
	
	
	
	
	
	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	public boolean isRecordable() {
		return isRecordable;
	}

	public void setRecordable(boolean isRecordable) {
		this.isRecordable = isRecordable;
	}

	public boolean isRecorded() {
		return isRecorded;
	}

	public void setRecorded(boolean isRecorded) {
		this.isRecorded = isRecorded;
	}


	public int getLeftVolume() {
		return leftVolume;
	}

	public void setLeftVolume(int leftVolume) {
		this.leftVolume = leftVolume;
	}

	public int getRightVolume() {
		return rightVolume;
	}

	public void setRightVolume(int rightVolume) {
		this.rightVolume = rightVolume;
	}


	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getTrackPath() {
		return trackPath;
	}

	public void setTrackPath(String trackPath) {
		this.trackPath = trackPath;
	}

	public String getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(String trackNumber) {
		this.trackNumber = trackNumber;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		
		this.songName = songName;
	}

	
	public int getBalance() {
		return balance;
	}
	
	
	

	public int getPlayCurrentPosition() {
		return playCurrentPosition;
	}



	public void setPlayCurrentPosition(int playCurrentPosition) {
		this.playCurrentPosition = playCurrentPosition;
	}



	public void setBalance(int balance) {
		this.balance = balance;
		this.setVolume();
	}
	
	
	
	
	
	/*
	* This method finalizes 'mediaPlayer' object
	* @author joaosantacruz.com
	*/
	public void finalize() throws Throwable {

	    // deallocate all memory
	    if (mediaPlayer != null) {
	        if (mediaPlayer.isPlaying()) {
	        	mediaPlayer.stop();
	        }
	        mediaPlayer.release();
	        mediaPlayer = null;
	    }
		super.finalize();
	}

}
