// MongoDB shell script to check count of products missing "id" field
var countMissing = db.products.countDocuments({
  $or: [
    { id: { $exists: false } },
    { id: null },
    { id: "" }
  ]
});
print("Number of products missing 'id' field: " + countMissing);
