package com.example.domowyogrodnik.models_adapters

import com.example.domowyogrodnik.db.PlantsDB
import java.io.Serializable

class PlantModel(val name: String?, val description: String?, val photo: String, val db_object: PlantsDB) :
    Serializable