const supabase = require('../config/supabase');

exports.register = async (req, res) => {
    try {
        const { username, password, nama_lengkap, no_hp, alamat, email } = req.body;
        if (!username || !password || !nama_lengkap || !no_hp || !alamat) {
            return res.status(400).json({ error: 'Semua kolom wajib diisi!' });
        }
        const { data: existingUser } = await supabase
            .from('users')
            .select('*')
            .eq('username', username)
            .single();
        if (existingUser) {
            return res.status(400).json({ error: 'Username sudah digunakan!' });
        }
        const bcrypt = require('bcrypt');
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        const { data, error } = await supabase
            .from('users')
            .insert([{
                username,
                password: hashedPassword, 
                nama_lengkap,
                no_telepon: no_hp,
                alamat_lengkap: alamat,
                email: email || null,
                role: 'nasabah'
            }])
            .select();
        if (error) throw error;
        res.status(201).json({
            message: 'Registrasi berhasil!',
            user: {
                id: data[0].id,
                username: data[0].username,
                nama_lengkap: data[0].nama_lengkap
            }
        });
    } catch (error) {
        console.error('Error saat registrasi:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.login = async (req, res) => {
    try {
        const { username, password } = req.body;
        if (!username || !password) {
            return res.status(400).json({ error: 'Username dan password wajib diisi!' });
        }
        const { data: user, error } = await supabase
            .from('users')
            .select('*')
            .eq('username', username)
            .single();

        if (error || !user) {
            return res.status(401).json({ error: 'Username atau password salah!' });
        }

        // Bandingkan password dengan bcrypt
        const bcrypt = require('bcrypt');
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(401).json({ error: 'Username atau password salah!' });
        }

        res.status(200).json({
            message: 'Login berhasil!',
            user: {
                id: user.id,
                username: user.username,
                role: user.role,
                nama_lengkap: user.nama_lengkap,
                email: user.email,
                no_telepon: user.no_telepon,
                alamat_lengkap: user.alamat_lengkap,
                created_at: user.created_at,
                saldo: user.saldo
            }
        });
    } catch (error) {
        console.error('Error saat login:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.getUserProfile = async (req, res) => {
    try {
        const { id } = req.params;
        const { data, error } = await supabase
            .from('users')
            .select('id, username, role, nama_lengkap, saldo')
            .eq('id', id)
            .single();
        if (error || !data) return res.status(404).json({ error: 'User tidak ditemukan' });
        res.status(200).json({ data });
    } catch (error) {
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.getNasabah = async (req, res) => {
    try {
        const { data, error } = await supabase
            .from('users')
            .select('id, username, nama_lengkap, saldo, no_telepon, alamat_lengkap')
            .eq('role', 'nasabah')
            .order('nama_lengkap', { ascending: true });
        
        if (error) throw error;
        res.status(200).json({
            message: 'Berhasil mengambil daftar nasabah',
            data: data
        });
    } catch (error) {
        console.error('Error get nasabah:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.updateNasabah = async (req, res) => {
    try {
        const { id } = req.params;
        const { username, password, nama_lengkap, no_hp, alamat } = req.body;
        
        let updateData = {
            username,
            nama_lengkap,
            no_telepon: no_hp,
            alamat_lengkap: alamat
        };

        if (password && password.trim() !== "") {
            const bcrypt = require('bcrypt');
            const salt = await bcrypt.genSalt(10);
            updateData.password = await bcrypt.hash(password, salt);
        }

        const { data, error } = await supabase
            .from('users')
            .update(updateData)
            .eq('id', id)
            .select();

        if (error) throw error;
        res.status(200).json({ message: 'Berhasil mengupdate nasabah', data });
    } catch (error) {
        console.error('Error update nasabah:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.deleteNasabah = async (req, res) => {
    try {
        const { id } = req.params;
        const { error } = await supabase
            .from('users')
            .delete()
            .eq('id', id);

        if (error) throw error;
        res.status(200).json({ message: 'Berhasil menghapus nasabah' });
    } catch (error) {
        console.error('Error delete nasabah:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};
