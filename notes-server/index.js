require("dotenv/config")
const express = require('express');
const mongoose = require('mongoose');
const noteRoutes = require('./notes/route');
const cors = require("cors")

const PORT = process.env.PORT || 3001;

const app = express();

app.use(cors())
app.use(express.json());

mongoose
  .connect(process.env.MONGO_URI)
  .then(() => {
    console.log("connect to mongo db");
  })
  .catch((err) => {
    console.error(err);
});

app.use('/api/notes', noteRoutes);

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
