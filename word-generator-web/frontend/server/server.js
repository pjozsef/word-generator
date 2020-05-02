const path = require('path');
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const app = express();
const publicPath = path.join(__dirname, '..', 'build');

app.use(express.static(publicPath));

const port = process.env.PORT || 8008;
const apiUrl = process.env.API_URL || 'http://localhost:8080';

app.use('/api', createProxyMiddleware({ target: apiUrl, changeOrigin: true }));

app.get('/*', (req, res) => {
    res.sendFile(path.join(publicPath, 'index.html'));
});

app.listen(port, () => {
    console.log(`Server is up on port ${port}`);
    console.log(`Proxying to ${apiUrl}`);
});
