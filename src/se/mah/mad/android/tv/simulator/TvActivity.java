package se.mah.mad.android.tv.simulator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import android.view.Display;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class TvActivity extends Activity {

	public static final String sdPath = "/mnt/sdcard/";
	/*
	 * The magic number is the one used to resize the markers after how large
	 * the screen is. Currently, the markers are sized after (screen width +
	 * screen height) * magic number.
	 */
	public static final float MAGICNUMBER = 0.05f;
	private VideoView videoView;
	private ImageView markerTL;
	private ImageView markerBR;
	private TextView debugText;
	private String mediaPath;
	private File[] videos;
	private float screenRatio;
	private int screenWidth;
	private int screenHeight;
	private int videoIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tv);

		/*
		 * Define where your videos are stored. Then load them into the program.
		 */
		this.mediaPath = sdPath + "videos/";
		loadVideos(mediaPath);
		this.videoIndex = 0;
		/*
		 * Find all the views used in this program
		 */
		this.videoView = (VideoView) findViewById(R.id.videoView);
		this.debugText = (TextView) findViewById(R.id.textView);
		this.markerTL = (ImageView) findViewById(R.id.markerTL);
		this.markerBR = (ImageView) findViewById(R.id.markerBR);
		/*
		 * Get screen specifications. This will load up the screen height, width
		 * and calculate a ratio which we will use for picking an appropriate
		 * marker.
		 */
		getScreenSpecs();

		/*
		 * Play the first video loaded.
		 */
		playVideo(videos[0]);
	}

	/**
	 * Creates an array of videofiles inside the specified path. The File object
	 * pretty much just stores a string with the filepath.
	 * 
	 * @param path
	 *            Path to video directory
	 */
	private void loadVideos(String path) {
		if (path != null || !path.equals("")) {
			File sdcard = new File(path);
			this.videos = sdcard.listFiles(new VideoFilter());
		} else {
			Log.i("Tv Activity", "Video file path is either empty or null.");
		}
	}

	/**
	 * This method will gather the specifications of the current display, the
	 * height, width and ratio. Notice that it will do this for the display and
	 * not the entire device screen, so this will not break in case you'd put
	 * the TV thingie inside a fragment.
	 */
	private void getScreenSpecs() {
		Display display = getWindowManager().getDefaultDisplay();
		this.screenHeight = display.getHeight();
		this.screenWidth = display.getWidth();
		this.screenRatio = (float) screenHeight / (float) screenWidth;
		display = null;

		this.debugText.setText("Display Width: " + screenWidth
				+ "\nDisplay Height: " + screenHeight + "\nDisplay Ratio: "
				+ screenRatio);

		/*
		 * A bigger screen naturally requires bigger markers.
		 */
		float lab = screenHeight + screenWidth;
		lab *= MAGICNUMBER;
		resizeMarkers((int) lab);
	}

	/**
	 * This method will resize the markers, maintaining aspect ratio.
	 * 
	 * @param newSize
	 *            New size for the markers
	 */
	public void resizeMarkers(int newSize) {
		if (this.markerTL != null && this.markerBR != null) {
			this.markerBR.setMinimumHeight(newSize);
			this.markerBR.setMinimumWidth(newSize);
			this.markerTL.setMinimumHeight(newSize);
			this.markerTL.setMinimumWidth(newSize);
		}
	}

	/*
	 * Media control -------------\
	 */

	public void pauseVideo() {
		if (this.videoView != null) {
			if (this.videoView.canPause()) {
				this.videoView.pause();
			}
		}
	}

	public void resumeVideo() {
		if (this.videoView != null) {
			this.videoView.resume();
		}
	}

	public void nextVideo() {
		if (videoIndex < (videos.length - 1)) {
			videoIndex++;
		} else {
			videoIndex = 0;
		}
		playVideo(videos[videoIndex]);
	}

	public void previousVideo() {
		if (videoIndex > 0) {
			videoIndex--;
		} else {
			videoIndex = videos.length - 1;
		}
		playVideo(videos[videoIndex]);
	}

	/*
	 * --------------/
	 */

	/**
	 * This method will simply play the selected video.
	 * 
	 * @param video
	 *            File object that points to a video on your filesystem.
	 */
	private void playVideo(File video) {
		if (video != null) {
			videoView.setVideoPath(video.getPath());
			videoView.setMediaController(new MediaController(this));
			videoView.requestFocus();
			videoView.start();
		} else {
			Log.i("Tv Activity",
					"Trying to play a video that is not specified.");
		}
	}

	/**
	 * A simple inner class used for finding files that end with .mp4 or .avi
	 * 
	 */
	class VideoFilter implements FilenameFilter {
		public boolean accept(File dir, String filename) {
			return (filename.endsWith(".mp4") || filename.endsWith(".avi"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_tv, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		this.videos = null;
		super.onDestroy();
	}
}

/*
 * THIS METHOD IS DEPRECATED -------------------------
 * 
 * This will resize the marker after whichever of height/width is the smallest.
 * It will then slightly scale the marker down, to keep some free area around
 * the border.
 * 
 * @param size
 * 
 * @param scale
 * 
 * public void resizeMarker(int height, int width, float scale) { float size =
 * (height < width) ? height : width; size *= 0.9; int marker = (int) size;
 * Toast.makeText(this, "Size: " + size + "\nFlingie: " + marker,
 * Toast.LENGTH_LONG).show(); if (size > 0) {
 * this.marker.setMinimumHeight(marker); this.marker.setMinimumWidth(marker); }
 * else { Log.i("Tv Activity", "Trying to set the marker to invalid size: " +
 * size); }
 */
