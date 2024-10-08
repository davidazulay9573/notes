const express = require('express');
const router = express.Router();
const noteController = require('./controller');

router.get('/', noteController.getAllNotes);
router.get('/:id', noteController.getNote);
router.post('/', noteController.createNote);
router.put('/:id', noteController.updateNote);
router.delete('/:id', noteController.deleteNote);
router.delete('/', noteController.deleteAllNotes);

module.exports = router;
