// MongoDB shell script to assign UUID string IDs to all products missing an ID
var cursor = db.products.find({
  $or: [
    { id: { $exists: false } },
    { id: null },
    { id: "" }
  ]
});

cursor.forEach(function(doc) {
  var newId = UUID().toString();
  print("Updating product _id: " + doc._id + " with new id: " + newId);
  db.products.updateOne(
    { _id: doc._id },
    { $set: { id: newId } }
  );
});
