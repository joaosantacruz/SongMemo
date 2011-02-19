/*
* SoundMemo Project
* @author joaosantacruz.com
* Created on 10/Feb/2011, 23:20:14
*/

package com.joaosantacruz.songmemo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import android.util.Log;


public class Song {

	static final String DEFAULT_SONG	= "UntitledSong";
	static final String APPDIR 			= "/sdcard/SongMemo/";
	static final String LYRICS_FILE 	= "lyrics.html";
	static final String SETTINGS_FILE 	= "settings.txt";

	private boolean isSongOpened 		= false;
	
	private boolean isRecordingSong 	= false;
	private boolean isPlayingSong 		= false;
	private boolean isPausedSong 		= false;
	private boolean isShowingLyrics 	= false;
	
	protected String songName 			= DEFAULT_SONG;
	protected String lyricsText			= "";
	

	ArrayList<Track> tracks 	= new ArrayList<Track>();
	
	
	public Song(SongMemo songMemo) {
		
		
		// Touch Application directory
		touchDirectory(APPDIR);	

		/* initialize Song */
		openSong(songName);
		
	}
	
	
	/*
	* This method creates a new song and open it 
	* @author joaosantacruz.com
	*/
	public String newSong(String songname) {
		
		String msg = "";
		
		if (touchDirectory(APPDIR + songname)) {
			msg = "The song '" + songname + "! Please insert another name.";
			openSong(songname);
		} else {
			msg = "The song '" + songname + "' was successfully created.";
			openSong(songname);
		}
		
		return (msg);

	}

	
	/*
	* This method Opens song and Initialize it 
	* @author joaosantacruz.com
	*/
	public String openSong(String songname) {

		// Close current opened Song
		if (isSongOpened) closeSong();
		
		// Change Song name
		this.songName=songname;

		// Touch Song's default directory
		touchDirectory(APPDIR + songName);
		
		// Touch Song's lyrics file
		touchFile(APPDIR + songName + "/" + LYRICS_FILE);

		// Touch Application directory
		touchFile(APPDIR + songName + "/" + SETTINGS_FILE);		
		
		tracks.add(new Track(this.songName, "01")); 		// TRACK1
		tracks.add(new Track(this.songName, "02")); 		// TRACK2
		tracks.add(new Track(this.songName, "03")); 		// TRACK3
		tracks.add(new Track(this.songName, "04")); 		// TRACK4
		//tracks.add(new Track(this.songName, "mix")); 		// RESERVED TO GLOBAL/MAIN MIX TRACK

		try {
			this.openSettings();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.setLyricsText(this.openLyrics());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		isSongOpened = true;

		return(this.songName);
		
	}
	


	/*
	* This method renames song (file)
	* @author joaosantacruz.com
	*/
	public String renameSong(String songname) {
		
		String actualSongName = this.songName;
		closeSong();
		Log.v("JOAO", "Rename SONG '" + actualSongName + " to " + songname + "' = = = = = = ");
		File orig_dir = new File(APPDIR + actualSongName);
		File dest_dir = new File(APPDIR + songname);
		orig_dir.renameTo(dest_dir);
		return (openSong(actualSongName));
		
	}
	

	/*
	* This method closes song
	* @author joaosantacruz.com
	*/
	public void closeSong(){
		
		saveSettings();
		
		for (Track track : tracks) {

			try {
				track.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.saveLyrics(this.getLyricsText());

		isSongOpened = false;
		
		tracks.removeAll(tracks);

		Log.v("JOAO", "CLOSE SONG '" + songName + "' = = = = = = ");
		
	}

	/* Open/Initialize Song */
	public boolean deleteSong() {
		closeSong();
		File f = new File(APPDIR + songName);
		deleteDirectory(f);
		Log.v("JOAO", "DELETE SONG '" + songName + "' = = = = = = ");
		openSong(DEFAULT_SONG);
		return (true);
	}
	


	
	   public boolean clearTrack(int trackNumber) {
		   File f = new File(tracks.get(trackNumber).getTrackPath());
		   if( f.exists() ) {
			   f.delete();
			   
			   try {
				   tracks.get(trackNumber).createTrackFile();
			   } catch (IOException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   }
			   tracks.get(trackNumber).setRecorded(false);
			   Log.v("JOAO", "CLEAR Track '" + tracks.get(trackNumber).getTrackPath() + "' = = = = = = ");
			   return(true);
		    }
		    return(false);
		 } 
	   
	
	  public boolean deleteDirectory(File path) {
		    if( path.exists() ) {
		    	File[] files = path.listFiles();
				for (File dir : files) {
		         if(dir.isDirectory()) {
		           deleteDirectory(dir);
		         }
		         else {
		        	 dir.delete();
		         }
		      }
		    }
		    return( path.delete() );
		  }


	/* Toggle individual track mute button */
	public boolean toggleMute(int TrackNumber) {

		if (tracks.get(TrackNumber).isMuted()) {
			tracks.get(TrackNumber).setMuted(false);
			
		} else {
			tracks.get(TrackNumber).setMuted(true);
		}

		//saveSettings();

		return (tracks.get(TrackNumber).isMuted());
		
	}

	
	
	/* Toggle individual track record button */
	public boolean toggleRecordable(int TrackNumber) {
		
		if(!isRecordingSong){
			if (tracks.get(TrackNumber).isRecordable()) {
				tracks.get(TrackNumber).setRecordable(false);
			} else {
				setAllTracksRecordable(false);
				tracks.get(TrackNumber).setRecordable(true);
			}
		}
		//saveSettings();
		return (tracks.get(TrackNumber).isRecordable());
		
	}
	
	
	
	
	/* Toggle song lyrics */
	public boolean toggleLyrics() {

		if (isShowingLyrics()) {
			setShowingLyrics(false);
		} else {
			setShowingLyrics(true);
		}

		return (isShowingLyrics());
		
	}
	
	
	

	
	public void setAllTracksRecordable(boolean value){
			for (Track track : tracks) {
				track.setRecordable(value);
			}
	}
	
	public void playAllTracks(){
		for (Track track : tracks) {
			if(!track.isMuted()){
				// TO DO
				
			}
		}
	}
	
	
	


	/* Stop Song Recording/Play */
	public boolean songStop() {
		//Log.v("JOAO", "STOPPPPPPPP: " + SONGMEMO.playButton.isSelected());
		for (Track track : tracks) {
			if(track.isPlaying()){
				track.stopPlaying();
				isPlayingSong 	= false;
			}
		}
		
	

		for (Track track : tracks) {
			if(track.isRecording()){
				track.stopRecording();
				isRecordingSong 	= false;
				track.setRecorded(true);
				//saveSettings();
			}
			
		}
		

		return (isPlayingSong && isRecordingSong);

	}

	/* Start Song Recording  */
	public boolean songRecord() {

		if(!isRecordingSong && !isPlayingSong){
			
			for (Track track : tracks) {
				if(track.isRecordable()){
					for (Track track2 : tracks) {
						if(!track2.isRecordable() && track2.isRecorded()){
							track2.startPlaying();
						}
					}
					track.startRecording();
					isRecordingSong 	= true;

				}
			}
	
			if(!isRecordingSong){
				//Toast.makeText(getApplicationContext(), "No track selected to record!", Toast.LENGTH_SHORT).show();			
				Log.v("JOAO", "Song - No track selected to record!");
			}
			
		}
		return isRecordingSong;

	}

	
	
	
	
	/* Play Song */
	public boolean songPlay(int currentPositionBarValue) {
		Log.v("JOAO", "SONG-pos-Bar: " + currentPositionBarValue );
		if(!isRecordingSong && !isPlayingSong){

			for (Track track : tracks) {

				if(track.isRecorded()){	
					
					track.startPlaying();
					
					isPlayingSong 	= true;
					
				}
			}
			
		}
		
		return isPlayingSong;

	}


	
	
	public void SaveToDB() {
		// TODO
		// save to sqlLight
	}


	public void clean() {
		// TODO
	}

	public void rename() {
		// TODO
	}

	public void renameTrack() {
		// TODO
	}

	public void mix() {
		// TODO
	}







	
	/* Create song directory and the 4 tracks files */
	public boolean createLyrics() {
			return (touchFile(APPDIR + "/" + songName + "/" +  LYRICS_FILE));
	}
	

	

	 public void saveLyrics(String lyrics_text) { 


       			File f = new File(APPDIR + songName + "/" + LYRICS_FILE);
       			Log.v("JOAO", "SAVES LYRISCS '" + lyrics_text + "' to " + APPDIR + songName + "/" + LYRICS_FILE);
       			FileWriter fw = null;
				try {
					fw = new FileWriter(f);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
               BufferedWriter out = new BufferedWriter(fw);
              			
               try {
				out.write(lyrics_text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
               try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
			 
			 
    }
	
	
		public String getLyricsText() {
			return lyricsText;
		}



		public void setLyricsText(String lyricsText) {
			this.lyricsText = lyricsText;
		}


	 // Read Lyrics 
   	 public String openLyrics() throws Exception {
   		 
   		 File f = new File(APPDIR + songName + "/" + LYRICS_FILE);
   		 FileReader fr = new FileReader(APPDIR + songName + "/" + LYRICS_FILE);
   		 BufferedReader br = new BufferedReader(fr);
   		 
   		 this.lyricsText = "";
   		 
   		 if(f.exists() && f.length()>1){
   			 String line = "";
    		 while((line = br.readLine()) != null) {
    			 	this.lyricsText = line;
    		 }
   
    		 fr.close();
   	 	}
   		
   		 Log.v("JOAO", "OPEN LYRISCS '" + lyricsText + "' from " + APPDIR + songName + "/" + LYRICS_FILE);
   		
   		 return(this.lyricsText);
   	 }
	
	
	
	
	
	
	/* If the directory does not exists: create-it */
	public boolean touchDirectory(String dirname) {
		File f = new File(dirname);
		if (f.exists()) {
			return true;
		} else {
			f.mkdirs();
			return false;
		}
	}
	
	
	/* If the directory does not exists: create-it */
	public boolean touchFile(String filename) {
		File f = new File(filename);
		if (f.exists()) {
			return true;
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	

	public File[] getSongsList() {

		File d= new File(APPDIR);
		return (d.listFiles());
		
	}



	public void setShowingLyrics(boolean isShowingLyrics) {
		this.isShowingLyrics = isShowingLyrics;
	}



	public boolean isShowingLyrics() {
		return isShowingLyrics;
	}



	public int volumeUp(int TrackNumber) {

		if(tracks.get(TrackNumber).getLeftVolume()<10){
			tracks.get(TrackNumber).setLeftVolume(tracks.get(TrackNumber).getLeftVolume()+1);
			tracks.get(TrackNumber).setRightVolume(tracks.get(TrackNumber).getRightVolume()+1);
			tracks.get(TrackNumber).setVolume();
		}

			saveSettings();

		return (tracks.get(TrackNumber).getLeftVolume());
		
	}

	public int volumeDown(int TrackNumber) {

		if(tracks.get(TrackNumber).getLeftVolume()>=1){
			tracks.get(TrackNumber).setLeftVolume(tracks.get(TrackNumber).getLeftVolume()-1);
			tracks.get(TrackNumber).setRightVolume(tracks.get(TrackNumber).getRightVolume()-1);
		}
		tracks.get(TrackNumber).setVolume();

			saveSettings();

		return (tracks.get(TrackNumber).getLeftVolume());
	}
	
	public String renameTrack(String trackName, int trackNumber){
		tracks.get(trackNumber).setTrackName(trackName);
		//Log.v("JOAO", "RENAME TRACK " + trackNumber + " to " + trackName);
		saveSettings();
		return(trackName);
	}
	
	
	
	
	
	public int maxTrackDuration(String typeOrindex) {

		// typeOrindex = track_duration or track_number
		
		int maxTrackDuration = 0;
		int maxTrackNumber = 0;
		int i = 0;
		
		for (Track track : tracks) {
			if(track.isRecorded() && !track.isRecording()){
				if(track.mediaPlayer != null){
					if (track.mediaPlayer.getDuration()>maxTrackDuration){
						maxTrackDuration = (int)track.mediaPlayer.getDuration();
						maxTrackNumber = i;
					}
				}
			}
			i ++;
		}
		
		if(typeOrindex=="track_number"){
			return (maxTrackNumber);
		}else{
			return (maxTrackDuration);
		}
		
	}
	
	public int setPlayPosition(int newPosition) {
		
		if(isPlayingSong){
			
			for (Track track : tracks) {
				track.mediaPlayer.seekTo((int)(newPosition*maxTrackDuration("track_duration")/100));
				//Log.v("JOAO", "Timeline ------- NewPosition:" +((int)(newPosition*maxTrackDuration("track_duration")/100)) +"/   || CurrectPosition" + track.mediaPlayer.getCurrentPosition());	
			}
		
		}
		return(newPosition);
	
	}
	
	
	 public void saveSettings() { 


		 			//Log.v("JOAO", "All tracks configuration was saved to " + APPDIR + songName + "/" + SETTINGS_FILE);

       			File f = new File(APPDIR + songName + "/" + SETTINGS_FILE);

       			FileWriter fw = null;
				try {
					fw = new FileWriter(f);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
               BufferedWriter out = new BufferedWriter(fw);
               
               String sep = ";";

               String songSettings = "";
               
			for (Track track : tracks) {
            	   songSettings += 		 track.getTrackName();
            	   songSettings += sep + track.getTrackPath();
            	   songSettings += sep + track.getTrackNumber();
            	   songSettings += sep + track.getSongName();
            	   songSettings += sep + track.getLastUpdate();
            	   
            	   songSettings += sep + track.isRecording();
            	   songSettings += sep + track.isPlaying();
            	   songSettings += sep + track.isMuted();
            	   songSettings += sep + track.isRecordable();
            	   songSettings += sep + track.isRecorded();
            	   songSettings += sep + track.getBalance();
    				
            	   songSettings += sep + track.getLeftVolume();
            	   songSettings += sep + track.getRightVolume() + "\r\n";
				
               }

			   //Log.v("JOAO", "\n\nTRACK values: \n\r" + songSettings);
			
			//	Log.v("JOAO", "SAVES - - -- - - -" + this.getLyricsText());
				
			
               try {
				out.write(songSettings);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
               try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           

 
			 
     }
	
	
	

	 	// Read settings 
    	 public void openSettings() throws Exception {
    		 
    		 File f = new File(APPDIR + songName + "/" + SETTINGS_FILE);
    		 FileReader fr = new FileReader(APPDIR + songName + "/" + SETTINGS_FILE);
    		 BufferedReader br = new BufferedReader(fr);
	    	
    		 if(f.exists() && f.length()>1){
	    		
    		 	String line;
	    		 int lineIndex = 0;
	    		 
	    		 String songSettings[];
	    		 
	    		 while((line = br.readLine()) != null) {
	    				
	    			 	songSettings =  line.split(";");
	    			 	//Log.v("JOAO", "CONFIGGGGG (" + lineIndex + ") - - - - - - - - - - -  - " + songSettings.length);
	    			 	
	    				tracks.get(lineIndex).setTrackName(songSettings[0]);
	    				tracks.get(lineIndex).setTrackPath(songSettings[1]);
	    				tracks.get(lineIndex).setTrackNumber(songSettings[2]);
	    				tracks.get(lineIndex).setSongName(songSettings[3]);
	    				tracks.get(lineIndex).setLastUpdate(songSettings[4]);
	
	    				tracks.get(lineIndex).setRecording(Boolean.parseBoolean(songSettings[5]));
	    				tracks.get(lineIndex).setPlaying(Boolean.parseBoolean(songSettings[6]));
	    				tracks.get(lineIndex).setMuted(Boolean.parseBoolean(songSettings[7]));
	    				tracks.get(lineIndex).setRecordable(Boolean.parseBoolean(songSettings[8]));
	    				tracks.get(lineIndex).setRecorded(Boolean.parseBoolean(songSettings[9]));
	    				tracks.get(lineIndex).setBalance(Integer.parseInt(songSettings[10]));
	
	    				tracks.get(lineIndex).setLeftVolume(Integer.parseInt(songSettings[11]));
	    				tracks.get(lineIndex).setRightVolume(Integer.parseInt(songSettings[12]));
	
	    				
	    				lineIndex ++;
	    		 
	    		 }
	    		 
	    		 fr.close();

	    		 
	    		 Log.v("JOAO", "All tracks configuration was loaded from " + APPDIR + songName + "/" + SETTINGS_FILE);
    	 	}
    		 
    	 }
	    		 

    		
    		public boolean isRecordingSong() {
    			return isRecordingSong;
    		}



    		public void setRecordingSong(boolean isRecordingSong) {
    			this.isRecordingSong = isRecordingSong;
    		}



    		public boolean isPlayingSong() {
    			return isPlayingSong;
    		}



    		public void setPlayingSong(boolean isPlayingSong) {
    			this.isPlayingSong = isPlayingSong;
    		}



    		public boolean isPausedSong() {
    			return isPausedSong;
    		}



    		public void setPausedSong(boolean isPausedSong) {
    			this.isPausedSong = isPausedSong;
    		}



    		public String getSongName() {
    			return songName;
    		}



    		public void setSongName(String songName) {
    			this.songName = songName;
    		}



			public void setBalance(int i, int progress) {
				tracks.get(i).setBalance(progress);
				//saveSettings();
			}



			public boolean soloTrack(int trackIndex) {
				Log.v("JOAO", "SOOOOOOOOOLOOOOOOOOOOOOOOOOOOOOOOOO '" + trackIndex);
				for (int i=0; i<tracks.size(); i++) {
					if(i!=trackIndex){
						tracks.get(i).setMuted(true);
					}else{
						tracks.get(i).setMuted(false);
					}
				}
				
				return true;
				
			}









	
	
}
