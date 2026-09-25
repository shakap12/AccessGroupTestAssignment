package com.example.hotels.model;

import java.util.List;

public record Hotel(String id, String name, List<RoomType> roomTypes, List<Room> rooms) {
}
