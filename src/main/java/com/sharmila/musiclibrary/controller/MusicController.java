package com.sharmila.musiclibrary.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharmila.musiclibrary.api.MusicManager;
import com.sharmila.musiclibrary.api.domain.Company;
import com.sharmila.musiclibrary.api.domain.Music;


@RestController
@RequestMapping(value="/music")
public class MusicController {
	
	@Autowired
	private MusicManager musicManager;
	
	private  List<Map<String,Object>> response=new ArrayList<>();
	
	@RequestMapping(method=RequestMethod.POST)
	public boolean createIndex(@RequestBody Music music){
		
		boolean reponse=musicManager.create(music);
		return reponse;
	}
	
	//TODO: user send id in param
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	public boolean updateIndex(@PathVariable(value="id")String id,@RequestBody Music music) throws IOException{
		boolean	response=musicManager.update(music,id);
		return response;
	}
	
//	TODO: add default sorting to desc order by modified date
//	user can send param sortby & sortorder
//	add pagination =user can send param page and size
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Map<String,Object>> search(
			@RequestParam(value="sortBy",required=false)String sortBy,
			@RequestParam(value="sortOrder",required=false)String sortOrder,
			@RequestParam(value="size",required=false)Integer size,
			@RequestParam(value="page",required=false)Integer page){
		Integer from=1;
		System.out.println("recieved size "+size);
		if(sortBy==null){
			sortBy="modifiedDate";
		}
		if(sortOrder==null){
			sortOrder="DESC";
		}
		
		if(size==null && page==null){
			size=10;
			from=0;
		}
		if(page!=null){
			from=(page-1)*size;
		}
		else{
			from=0;
		}
		
		System.out.println("sort by "+sortBy +" sort order "+sortOrder + " size "+size +" from "+from );
		response=	musicManager.searchAll(sortBy, sortOrder, size, from);


		return response;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public List<Map<String, Object>>  getById(@PathVariable(value="id")String id){
	
		 response=musicManager.getById(id);
		System.out.println(response);
		return response;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public boolean deleteIndexItems(@PathVariable(value="id")String id){
		System.out.println("delete api called : id"+id);
		boolean response=musicManager.delete(id);
		
		return response;
	}
	
	
	@RequestMapping(value="/bulk",method=RequestMethod.POST)
	public String insertBulk(@RequestBody String companyList){
		
		musicManager.bulk(companyList);
		return "bulk insert";
	}
	
	
}
