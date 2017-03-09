package com.sharmila.musiclibrary.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharmila.musiclibrary.api.MusicManager;
import com.sharmila.musiclibrary.api.domain.Music;

@RestController
public class MusicController {
	
	@Autowired
	private MusicManager musicManager;
	
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public String createIndex(@RequestBody Music music){
		
		System.out.println(music.getId());
		musicManager.create(music);
		return "index created";
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public String updateIndex(@RequestBody Music music){
		musicManager.update(music);
		return "index updated";
	}
	
	@RequestMapping(value="/getResponse/{id}",method=RequestMethod.GET)
	public String getIndex(@PathVariable(value="id")String id){
	
		String response=musicManager.getById(id);
		System.out.println(response);
		
		String value=new String(response.getBytes());
		return value;
	}
	
	@RequestMapping(value="/delete/{id}",method=RequestMethod.POST)
	public String deleteIndexItems(@PathVariable(value="id")String id){
		System.out.println("delete api called : id"+id);
		String response=musicManager.delete(id);
		
		
		return response;
	}
	
	
}
