package com.sharmila.musiclibrary.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sharmila.musiclibrary.api.RandomManager;
import com.sharmila.musiclibrary.repository.RandomRepository;

@Service
public class RandomManagerImpl implements RandomManager{

	@Autowired
	private RandomRepository randomRepository;

	@Override
	public List<Map<String, String>> searchAll() {
		
		return randomRepository.getCompanyLocation();
	}

}
