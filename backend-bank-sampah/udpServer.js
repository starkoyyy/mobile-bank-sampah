const dgram = require('dgram');

function startUdpServer() {
    const server = dgram.createSocket('udp4');

    server.on('error', (err) => {
        console.log(`UDP server error:\n${err.stack}`);
        server.close();
    });

    server.on('message', (msg, rinfo) => {
        const message = msg.toString().trim();
        if (message === 'DISCOVER_BANK_SAMPAH_SERVER') {
            const reply = Buffer.from('BANK_SAMPAH_SERVER_ACK');
            server.send(reply, 0, reply.length, rinfo.port, rinfo.address, (err) => {
                if (err) {
                    console.error('Error sending UDP reply:', err);
                } else {
                    console.log(`Sent auto-discovery reply to Android app at ${rinfo.address}:${rinfo.port}`);
                }
            });
        }
    });

    server.on('listening', () => {
        const address = server.address();
        console.log(`UDP Auto-Discovery Server listening on port ${address.port}`);
    });

    // Bind to 41234 port
    server.bind(41234);
}

module.exports = startUdpServer;
