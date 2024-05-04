package com.example.semana06.service;

import com.example.semana06.entity.Libro;
import com.example.semana06.entity.Pais;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServiceLibro {
    @POST("servicio/libro")
    public Call<Libro> registra(@Body Libro objLibro);
}
