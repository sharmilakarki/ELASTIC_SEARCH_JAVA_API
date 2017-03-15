package com.sharmila.musiclibrary.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sharmila.musiclibrary.api.MusicManager;
import com.sharmila.musiclibrary.api.domain.Music;

@RestController
public class MusicController {
	
	@Autowired
	private MusicManager musicManager;
	
	private  List<Map<String,Object>> response=new ArrayList<>();
	
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
	
	
	@RequestMapping(value="/bulk",method=RequestMethod.POST)
	public String insertBulk(@RequestBody List<Music> music){
		
		musicManager.bulkTest(music);
		return "bulk insert";
	}
	
	
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public List<Map<String,Object>> search(){
		
	
		response=	musicManager.searchAll();


		return response;
	}
	
	@RequestMapping(value="/sortasc/{fieldName}",method=RequestMethod.GET)
	public List<Map<String,Object>> sortByAscOrder(@PathVariable(value="fieldName")String fieldName){
		
		
		response=	musicManager.sortByAscOrder(fieldName);


		return response;
	}
	
	@RequestMapping(value="/sortdesc/{fieldName}",method=RequestMethod.GET)
	public List<Map<String,Object>> sortByDescOrder(@PathVariable(value="fieldName")String fieldName){
		
		
		response=	musicManager.sortByDescOrder(fieldName);


		return response;
	}
	
	@RequestMapping(value="/sort/{fieldName}",method=RequestMethod.GET)
	public List<Map<String,Object>> sortByAscPrice(@PathVariable(value="fieldName")String fieldName){
		
		
		response=	musicManager.sortPrice(fieldName);


		return response;
	}
}
