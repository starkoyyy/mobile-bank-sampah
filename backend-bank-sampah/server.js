require('dotenv').config();
const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const apiRoutes = require('./src/routes/api');

const app = express();
const PORT = process.env.PORT || 3000;

// Start UDP Server for Android Auto-Discovery
const startUdpServer = require('./udpServer');
startUdpServer();

app.use(cors());
app.use(express.json());

// Serve uploads folder statically for photos
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Basic Logger Middleware
app.use((req, res, next) => {
    let urlChars = [];
    for(let i=0; i<req.url.length; i++) urlChars.push(req.url.charCodeAt(i));
    const log = `[${new Date().toISOString()}] ${req.method} ${req.url} (chars: ${urlChars.join(',')})\nBody: ${JSON.stringify(req.body)}\n\n`;
    console.log(log);
    try {
        fs.appendFileSync('request_logs.txt', log);
    } catch (e) {}
    next();
});

// API Routes
app.use('/api', apiRoutes);

// Root Endpoint
app.get('/', (req, res) => {
    res.send('API Bank Sampah Berjalan dengan Baik!');
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server berjalan di http://0.0.0.0:${PORT}`);
});