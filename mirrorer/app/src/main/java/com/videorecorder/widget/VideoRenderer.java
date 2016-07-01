package com.videorecorder.widget;
 
import java.nio.ByteBuffer;

import com.bdj.video.build.NativeSprFilter;
import com.videorecorder.util.Log;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoRenderer {

	// Video surface
	private static SurfaceView surface;
	
	// Video frames
	private int decoded[];
	private Bitmap converted;
	private byte pixel[];
	ByteBuffer buffer;
	
	// Player status
	private boolean started = false;
	
    private Canvas canvas;
	private SurfaceHolder holder;

	// Constructor
	public VideoRenderer(SurfaceView renderer) {
		surface = renderer;
		holder = renderer.getHolder();
		
		int width = 480;
		int height = 480;
		
		//decoded = new int[width*height];
		converted = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    pixel = new byte[(width+15)/16*16*((height+15)/16*16)*2];
		buffer = ByteBuffer.wrap( pixel );
	}


	// Start the player
	public void start() {
		started = true;
		
		threadRender.start();
	}
	
	// Stop the renderer
	public void stop() {
		started = false;
	}

	Thread threadRender = new Thread() {
		public void run() {
			try {
			    loge("threadRender start!");
				PlayAudio playTask = new PlayAudio();
				playTask.execute();
				while (started) {
					try {
					    loge("threadRender!");
					    long RenderFrameTime = System.currentTimeMillis();
						int ret = RenderFrame();
						Log.d("threadRender", "millis:"+(System.currentTimeMillis()-RenderFrameTime));
						if (ret < 0)
						{
							sleep(1000/200);
						}
						else
						{
							sleep(1000/60);
						}
					}catch (Exception e) {
		        		loge("threadRender while error " + e.toString());
		        	}
				}
				playTask.stop();
			}catch (Exception e)
			{
				loge("threadRender ERROR!!!" + e.toString());
			}
		}
	};
	//threadCodec3.start();//*/
	
	public int RenderFrame() {
	    loge("RenderFrame!");
//		result = dec3.VideoDecodeChannelFrame(pixel);
		int[] width = new int[]{0};
		int[] height = new int[]{0};
		int[] format = new int[]{NativeSprFilter.R_FILET_NONE};
		long nativeGetFrameTime = System.currentTimeMillis();
		byte[] frame = NativeSprFilter.SPRFILTER_GetFilterVideoFrame(width, height, format);
		Log.d("threadRender", " SPRFILTER_GetFilterVideoFrame after millis:"+(System.currentTimeMillis() - nativeGetFrameTime));
		if (frame != null)
		{
			converted = Bitmap.createBitmap(width[0], height[0], Bitmap.Config.RGB_565);
			buffer = ByteBuffer.wrap(frame);
			
			converted.copyPixelsFromBuffer(buffer);
			buffer.clear();

			if (converted != null && surface != null) {

				//if (surfaceCreated && surfaceChanged){
					canvas = null;
					try {				
						synchronized (holder) {
							canvas = holder.lockCanvas();
						}
					} catch(Exception e){ 
					    e.printStackTrace();
					} finally {

						logv("canvas = " + canvas);
						if (canvas != null){
							//canvas.drawARGB(255, 0, 0, 0);
							// Then draw bmp
						    long timedrawBitmap = System.currentTimeMillis();
							canvas.drawBitmap(converted, null, canvas.getClipBounds(), null);
							Log.d("threadRender", " drawBitmap millis:"+(System.currentTimeMillis() - timedrawBitmap));
							holder.unlockCanvasAndPost(canvas);

							logv("canvas.drawBitmap success!");
						}
					}
				//}
			}
			else
			{
				logv("converted = " + converted);
				logv("surface = " + surface);
			}
		}
		else
		{
			loge("DecodeAndConvert error!" + frame);
		}
		
		if (frame != null)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	
    static private void log(String msg)
    {
    	Log.v("cxx", "[VideoRenderer]: " + msg);
    }
    static private void logv(String msg)
    {
    	Log.v("cxx", "[VideoRenderer]: " + msg);
    }
    static private void loge(String msg)
    {
    	Log.e("cxx", "[VideoRenderer]: " + msg);
    }
    

	private class PlayAudio extends AsyncTask<Void, integer, Void> 
	{
		private boolean isPlaying = false;
		public void stop()
		{
			isPlaying = false;
		}
		protected Void doInBackground(Void... params) 
		{
	    	Log.e("cxx", "[doInBackground] [00]*************************");

			try 
			{
				int frequency = 16000;
				int channel = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
				//int channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
				int format = AudioFormat.ENCODING_PCM_16BIT;
				int bufferSize = AudioTrack.getMinBufferSize(frequency, channel, format);
				
				int[] channelArr = new int[]{channel};
				int[] sampleRateArr = new int[]{frequency};
				int[] formatArr = new int[]{format};
				
				// 
				AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,// 
						channel, format, bufferSize,// 
						AudioTrack.MODE_STREAM);// 
				audioTrack.play();

				isPlaying = true;
				// 
				while (isPlaying) {
					int i = 0;

			    	Log.e("cxx", "[doInBackground] [55]*************************");
			    	
					short[] data = NativeSprFilter.SPRFILTER_GetFilterAudioFrame(channelArr, sampleRateArr, formatArr);
					Log.e("cxx", "[doInBackground] [66]*************************");

					if (data != null)
					{
						audioTrack.write(data, 0, data.length);
					}
					else
					{
						java.lang.Thread.sleep(1000/200);
					}
				}
				audioTrack.stop();
			}
			catch (Exception e) 
			{
				loge("" + e);
		    	Log.e("cxx", "[doInBackground] [88] ERROR! " + e);
			}

	    	Log.e("cxx", "[doInBackground] [99]*************************");
			return null;
		}

		/**
		 * 
		 * 
		 */

		protected void onPreExecute() 
		{
			super.onPreExecute();
		}
	}
}
