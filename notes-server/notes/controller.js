const Note = require('./model');

exports.getAllNotes = async (req, res) => {
    try {
        const notes = await Note.find();
        const formattedNotes = notes.map(note => formatNote(note)); 
        res.status(200).json(formattedNotes);
    } catch (error) {
        res.status(500).json({ message: 'Error retrieving notes', error });
    }
};

exports.getNote = async (req, res) => {
    const { id } = req.params;
    
    try {
        const note = await Note.findById(id);
        if (!note) {
            return res.status(404).json({ message: 'Note not found' });
        }
        res.status(200).json(formatNote(note));
    } catch (error) {
        res.status(500).json({ message: 'Error retrieving note', error });
    }
};

exports.createNote = async (req, res) => {
    const { title, description } = req.body;
    
    const newNote = new Note({
        title,
        description,
    });
    
    try {
        const savedNote = await newNote.save();
        res.status(201).json(formatNote(savedNote));
    } catch (error) {
        res.status(500).json({ message: 'Error creating note', error });
    }
};

exports.updateNote = async (req, res) => {
    const { id } = req.params;
    
    try {
        const updatedNote = await Note.findByIdAndUpdate(id, req.body, { new: true });
        if (!updatedNote) {
            return res.status(404).json({ message: 'Note not found' });
        }
        res.status(200).json(formatNote(updatedNote));
    } catch (error) {
        res.status(500).json({ message: 'Error updating note', error });
    }
};

exports.deleteNote = async (req, res) => {
    const { id } = req.params;
    
    try {
        const deletedNote = await Note.findByIdAndDelete(id);
        if (!deletedNote) {
            return res.status(404).json({ message: 'Note not found' });
        }
        res.status(200).json({ message: 'Note deleted successfully' });
    } catch (error) {
        res.status(500).json({ message: 'Error deleting note', error });
    }
};

exports.deleteAllNotes = async (req, res) => {
    try {
        await Note.deleteMany({});
        res.status(200).json({ message: 'All notes deleted successfully' });
    } catch (error) {
        res.status(500).json({ message: 'Error deleting notes', error });
    }
};

/* ------------------------ */
function formatNote(note){
    const formattedNote = note.toObject();
    formattedNote.id = formattedNote._id;
    delete formattedNote._id;
    delete formattedNote.__v;
    return formattedNote;
};