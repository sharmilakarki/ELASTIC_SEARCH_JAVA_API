package com.sharmila.musiclibrary.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sharmila.musiclibrary.api.MusicManager;
import com.sharmila.musiclibrary.api.domain.Music;
import com.sharmila.musiclibrary.api.domain.SearchTerms;
import com.sharmila.musiclibrary.repository.MusicRepository;


@Service
public class MusicManagerImpl implements MusicManager{

	private static final Logger logger=LoggerFactory.getLogger(MusicManagerImpl.class);
	@Autowired
	private MusicRepository musicRepository;
	

	@Override
	public boolean create(Music music) {
		music.setCreatedDate(new Date());
		music.setModifiedDate(new Date());
		boolean response=musicRepository.create(music);
		return response;
	}

	@Override
	public boolean delete(String id) {
		
		return musicRepository.delete(id);
	}

	@Override
	public boolean update(Music music,String id) throws IOException {
		

			music.setModifiedDate(new Date());
			return musicRepository.update(music,id);
	
	}

	@Override
	public List<Map<String, Object>>  getById(String id) {
		
		return musicRepository.getById(id);
	}

	

	@Override
	public void bulkTest(List<Music> music) {
		
		 musicRepository.bulkTest(music);
	}

	

	@Override
	public List<Map<String,Object>>   searchAll(String sortBy,String sortOrder,int size,int from) {
		
		return musicRepository.searchAll(sortBy, sortOrder, size, from);
	}

	
	
	
}
