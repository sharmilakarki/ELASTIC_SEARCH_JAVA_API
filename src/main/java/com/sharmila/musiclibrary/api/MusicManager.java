package com.sharmila.musiclibrary.api;

import com.sharmila.musiclibrary.api.domain.Music;

public interface MusicManager {
	void create(Music music);
	String delete(String id);
	void update(Music music);
	String getById(String id);
}
