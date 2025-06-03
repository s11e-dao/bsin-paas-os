class WebSocketManager {
    
    constructor(baseUrl, token) {
        if (!baseUrl) {
            throw new Error('WebSocket URL is not defined');
        }
        this.baseUrl = baseUrl;
        this.token = token;
        this.sockets = new Map(); // 存储不同 toNo 的 WebSocket 实例
    }

    /**
     * 创建 WebSocket 连接
     * @param {string} toNo - 目标编号，唯一标识 WebSocket 连接
     * @param {function} onMessage - 消息接收回调
     * @param {function} onOpen - 连接成功回调
     * @param {function} onClose - 连接关闭回调
     * @param {function} onError - 错误回调
     */
    connect(toNo, onMessage, onOpen, onClose, onError) {
        console.log("this.sockets.has(toNo)")
        console.log(this.sockets.has(toNo))
        if (this.sockets.has(toNo)) {
            console.warn(`WebSocket for ${toNo} already exists.`);
            return this.sockets.get(toNo);
        }

        const socket = new WebSocket(`${this.baseUrl}${toNo}`, this.token);

        socket.onopen = () => {
            console.log(`WebSocket ${toNo} 连接成功`);
            if (onOpen) onOpen();
        };

        socket.onmessage = (event) => {
            console.log(`收到 WebSocket 消息 [${toNo}]:`, event.data);
            if (onMessage) onMessage(event.data);
        };

        socket.onclose = () => {
            console.log(`WebSocket ${toNo} 连接关闭`);
            this.sockets.delete(toNo); // 连接关闭时移除 WebSocket
            if (onClose) onClose();
        };

        socket.onerror = (error) => {
            console.error(`WebSocket ${toNo} 发生错误:`, error);
            if (onError) onError(error);
        };
        console.log('toNo', toNo)
        this.sockets.set(toNo, socket);
        console.log('this.sockets', this.sockets)
        return socket;
    }

    /**
     * 发送消息
     * @param {string} toNo - 目标编号
     * @param {any} message - 要发送的消息
     */
    sendMessage(toNo, message) {
        console.log('this.sockets', this.sockets)
        const socket = this.sockets.get(toNo);
        if (socket && socket.readyState === WebSocket.OPEN) {
            socket.send(JSON.stringify(message));
        } else {
            console.warn(`WebSocket ${toNo} 未连接，无法发送消息`);
        }
    }

    /**
     * 关闭 WebSocket 连接
     * @param {string} toNo - 目标编号
     */
    close(toNo) {
        console.log("-------close-------")
        console.log(
            
        )
        const socket = this.sockets.get(toNo);
        console.log(socket)
        if (socket) {
            socket.close();
            this.sockets.delete(toNo);
        }
    }

    /**
     * 关闭所有 WebSocket 连接
     */
    closeAll() {
        this.sockets.forEach((socket, toNo) => {
            console.log(`关闭 WebSocket: ${toNo}`);
            socket.close();
        });
        this.sockets.clear();
    }
}

export default WebSocketManager;
