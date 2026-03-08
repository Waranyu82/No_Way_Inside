package Client.Client;

import java.net.URL;
import javax.sound.sampled.*;

public class Sound {

    private static final int SOUND_COUNT = 7;
    private Clip musicClip;

    private final byte[][] seData = new byte[SOUND_COUNT][];
    private final AudioFormat[] seFormat = new AudioFormat[SOUND_COUNT];

    // Pool ของ Clip สำหรับ SE (เพื่อให้เสียงซ้อนกันได้)
    private static final int POOL_SIZE = 4;
    private final Clip[][] sePool = new Clip[SOUND_COUNT][POOL_SIZE];
    private final int[] sePoolIdx = new int[SOUND_COUNT];

    private final URL[] soundURL = new URL[SOUND_COUNT];

    FloatControl fcMusic;
    FloatControl [][] fcSE = new FloatControl[SOUND_COUNT][POOL_SIZE];

    public int musicVolumeScale = 3;
    public int seVolumeScale = 3;
    float volume;

    public Sound() {
        soundURL[0] = getClass().getResource("/Client/res/sound/song.wav");
        soundURL[1] = getClass().getResource("/Client/res/sound/click.wav");
        soundURL[2] = getClass().getResource("/Client/res/sound/Heart.wav");
        soundURL[3] = getClass().getResource("/Client/res/sound/pick_lantern.wav");
        soundURL[4] = getClass().getResource("/Client/res/sound/Foot.wav");
        soundURL[5] = getClass().getResource("/Client/res/sound/Door.wav");
        soundURL[6] = getClass().getResource("/Client/res/sound/Scream.wav");

        preloadAll();
    }

    private void preloadAll() {
        for (int i = 0; i < SOUND_COUNT; i++) {
            if (soundURL[i] == null)
                continue;
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
                AudioFormat fmt = ais.getFormat();
                byte[] data = ais.readAllBytes();
                ais.close();

                if (i == 0) {
                    // BGM: สร้าง Clip เดียวสำหรับ loop
                    musicClip = AudioSystem.getClip();
                    musicClip.open(fmt, data, 0, data.length);

                    //Music control
                    fcMusic = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                } else {
                    // SE: เก็บ raw data + สร้าง pool
                    seData[i] = data;
                    seFormat[i] = fmt;
                    for (int p = 0; p < POOL_SIZE; p++) {
                        sePool[i][p] = AudioSystem.getClip();
                        sePool[i][p].open(fmt, data, 0, data.length);

                        //Se control
                        fcSE[i][p] = (FloatControl) sePool[i][p].getControl(FloatControl.Type.MASTER_GAIN);
                    }
                }
            } catch (Exception e) {
                System.err.println("Sound preload failed [" + i + "]: " + e.getMessage());
            }
        }
    }

    /** เล่น BGM (index 0 เสมอ) */
    public void playMusic() {
        if (musicClip == null)
            return;
        musicClip.setFramePosition(0);
        musicClip.start();
    }

    public void loopMusic() {
        if (musicClip == null)
            return;
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        if (musicClip != null)
            musicClip.stop();
    }

    public void checkMusicVolume(){

        switch (musicVolumeScale){
            case 0 : volume = -80f; break;
            case 1 : volume = -20f; break;
            case 2 : volume = -12f; break;
            case 3 : volume = -5f; break;
            case 4 : volume = 1f; break;
            case 5 : volume = 6f; break;
        }

        fcMusic.setValue(volume);
    }

    public void checkSeVolume(){

        switch (seVolumeScale){
            case 0 : volume = -80f; break;
            case 1 : volume = -20f; break;
            case 2 : volume = -12f; break;
            case 3 : volume = -5f; break;
            case 4 : volume = 1f; break;
            case 5 : volume = 6f; break;
        }

        for (int i = 1; i < SOUND_COUNT; i++){
            for (int p = 1; p < POOL_SIZE; p++){
                if (fcSE[i][p] != null){
                    fcSE[i][p].setValue(volume);
                }
            }
        }
    }

    // ─── SE ────────────────────────────────────────────────
    /** เล่น SE ตาม index (1-6) */
    public void playSE(int i) {
        if (i < 1 || i >= SOUND_COUNT || seData[i] == null)
            return;

        Clip clip = sePool[i][sePoolIdx[i]];
        sePoolIdx[i] = (sePoolIdx[i] + 1) % POOL_SIZE;

        if (clip.isRunning())
            clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    // ─── Legacy API (ให้ GamePanel เรียกได้เหมือนเดิม) ─────

    /** @deprecated ใช้ playMusic() / playSE() แทน */
    public void setFile(int i) {
        /* no-op — preloaded */ }

    /** @deprecated */
    public void play() {
        /* no-op */ }

    /** @deprecated */
    public void loop() {
        loopMusic();
    }

    /** @deprecated */
    public void stop() {
        stopMusic();
    }
}