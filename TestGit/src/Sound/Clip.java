/**
 * Clip class holds an AudioClip and a boolean denoting if it is currently looping.
 * 
 * Made so the main Game class has a better idea if an audio clip is already playing or not.
 */

package Sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * Clip class holds an AudioClip and a boolean denoting if it is currently looping.
 * 
 * Made so the main Game class has a better idea if an audio clip is already playing or not.
 */
public class Clip
{
  
  /** The AudioClip. */
  private AudioClip stepClip;
  
  /** The boolean to know if the AudioClip is currently looping */
  private boolean isLooped;

  /**
   * Instantiates a new clip.
   *
   * @param url the path to the AudioClip
   */
  public Clip(URL url)
  {
    stepClip = Applet.newAudioClip(url);
    isLooped = false;
  }

  /**
   * Checks if the AudioClip is currently looping
   *
   * @return true, if the clip is in a loop
   */
  public boolean isLooped()
  {
    return isLooped;
  }

  /**
   * Loops the AudioClip and sets isLooped to true
   */
  public void setLoop()
  {
    isLooped = true;
    stepClip.loop();
  }

  /**
   * Stops the loop for the AudioClip and sets isLooped to false.
   */
  public void stopLoop()
  {
    isLooped = false;
    stepClip.stop();
  }
}