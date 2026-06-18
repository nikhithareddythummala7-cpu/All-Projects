package com.server.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "domains")
public class Domain {
    public String _id;
    public String name;
    public String description;

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
