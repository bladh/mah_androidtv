package se.mah.mad.android.tv.simulator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class TvActivity extends Activity {

	public static final String sdPath = "/mnt/sdcard/";
	private String mediaPath;
	private File[] videos;
	private VideoView videoView;
	private ImageView marker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tv);

		/**
		 * Define where your videos are stored.
		 */

		this.mediaPath = sdPath + "videos/";
		loadVideos(mediaPath);
		this.videoView = (VideoView) findViewById(R.id.videoView);
		this.marker = (ImageView) findViewById(R.id.imageView1);
		resizeMarker(600);
		playVideo(videos[0]);
	}

	/**
	 * Creates an array of filenames for videofiles inside the specified path.
	 * 
	 * @param path
	 *            Path to video directory
	 */
	private void loadVideos(String path) {
		if (path != null || !path.equals("")) {
			File sdcard = new File(path);
			this.videos = sdcard.listFiles(new VideoFilter());
		}else{
			Log.i("Tv Activity","Video file path is either empty or null.");
		}
	}

	public void resizeMarker(int size) {
		if (size > 0) {
			this.marker.setMinimumHeight(size);
			this.marker.setMinimumWidth(size);
		} else {
			Log.i("Tv Activity", "Trying to set the marker to invalid size: "
					+ size);
		}
	}

	private void playVideo(File video) {
		if (video != null) {
			videoView.setVideoPath(video.getPath());
			videoView.setMediaController(new MediaController(this));
			videoView.requestFocus();
			videoView.start();
		}else{
			Log.i("Tv Activity","Trying to play a video that is not specified.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_tv, menu);
		return true;
	}

	class VideoFilter implements FilenameFilter {
		public boolean accept(File dir, String filename) {
			return (filename.endsWith(".mp4") || filename.endsWith(".avi"));
		}
	}
}
