/*
 * Clip class holds an AudioClip and a boolean denoting if it is currently looping.
 * 
 * Made so the main Game class has a better idea if an audio clip is already playing or not.
 */
package Sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class Clip
{
  private AudioClip stepClip;
  private boolean isLooped;

  public Clip(URL url)
  {
    stepClip = Applet.newAudioClip(url);
    isLooped = false;
  }

  public boolean isLooped()
  {
    return isLooped;
  }

  public void setLoop()
  {
    isLooped = true;
    stepClip.loop();
  }

  public void stopLoop()
  {
    isLooped = false;
    stepClip.stop();
  }
}