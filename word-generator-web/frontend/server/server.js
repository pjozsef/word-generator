const path = require('path');
const express = require('express');
const httpProxy = require('http-proxy');
const apiProxy = httpProxy.createProxyServer();
const app = express();
const publicPath = path.join(__dirname, '..', 'build');

app.use(express.static(publicPath));

const port = process.env.PORT || 8008;
const apiUrl = process.env.API_URL || 'http://localhost:8080';

app.all('/api/*', (req, res)=> {
    apiProxy.web(req, res, {target: apiUrl});
})

app.get('/*', (req, res) => {
    res.sendFile(path.join(publicPath, 'index.html'));
});

app.listen(port, () => {
    console.log(`Server is up on port ${port}`);
    console.log(`Proxying to ${apiUrl}`);
});
