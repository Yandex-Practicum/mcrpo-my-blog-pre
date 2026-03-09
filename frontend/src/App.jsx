import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useParams, useNavigate } from 'react-router-dom';
import './App.css';

const API_URL = 'http://localhost:8080/api';

// Главная страница со списком постов
function HomePage() {
  const [posts, setPosts] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadPosts();
  }, [page, search]);

  const loadPosts = async () => {
    setLoading(true);
    try {
      const response = await fetch(
        `${API_URL}/posts?search=${encodeURIComponent(search)}&pageNumber=${page}&pageSize=10`
      );
      const data = await response.json();
      setPosts(data.posts || []);
      setTotalPages(data.totalPages || 1);
    } catch (error) {
      console.error('Ошибка загрузки постов:', error);
      setPosts([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <header className="header">
        <h1>📝 Мой Блог</h1>
        <Link to="/create" className="btn btn-primary">Создать пост</Link>
      </header>

      <div className="search-box">
        <input
          type="text"
          placeholder="Поиск по постам..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(1);
          }}
          className="search-input"
        />
      </div>

      {loading ? (
        <div className="loading">Загрузка...</div>
      ) : posts.length === 0 ? (
        <div className="no-posts">
          <p>Постов пока нет. Создайте первый!</p>
        </div>
      ) : (
        <div className="posts-grid">
          {posts.map(post => (
            <PostCard key={post.id} post={post} onUpdate={loadPosts} />
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setPage(p => Math.max(1, p - 1))}
            disabled={page === 1}
            className="btn btn-secondary"
          >
            ← Назад
          </button>
          <span className="page-info">Страница {page} из {totalPages}</span>
          <button
            onClick={() => setPage(p => Math.min(totalPages, p + 1))}
            disabled={page === totalPages}
            className="btn btn-secondary"
          >
            Вперёд →
          </button>
        </div>
      )}
    </div>
  );
}

// Карточка поста
function PostCard({ post, onUpdate }) {
  const navigate = useNavigate();
  const [liked, setLiked] = useState(false);
  const [likesCount, setLikesCount] = useState(post.likesCount || 0);

  const handleLike = async (e) => {
    e.stopPropagation();
    try {
      const method = liked ? 'DELETE' : 'POST';
      const response = await fetch(`${API_URL}/posts/${post.id}/likes`, { method });
      const newCount = await response.json();
      setLikesCount(newCount);
      setLiked(!liked);
    } catch (error) {
      console.error('Ошибка при обработке лайка:', error);
    }
  };

  return (
    <div className="post-card" onClick={() => navigate(`/posts/${post.id}`)}>
      {post.id && (
        <img
          src={`${API_URL}/posts/${post.id}/image`}
          alt={post.title}
          className="post-image"
          onError={(e) => {
            e.target.style.display = 'none';
          }}
        />
      )}
      <div className="post-content">
        <h2>{post.title}</h2>
        <p className="post-text">{post.text?.substring(0, 150)}...</p>
        <div className="post-tags">
          {post.tags?.map((tag, i) => (
            <span key={i} className="tag">#{tag}</span>
          ))}
        </div>
        <div className="post-footer">
          <button
            onClick={handleLike}
            className={`btn-icon ${liked ? 'liked' : ''}`}
          >
            {liked ? '❤️' : '🤍'} {likesCount}
          </button>
          <span>💬 {post.commentsCount || 0}</span>
        </div>
      </div>
    </div>
  );
}

// ============================================
// НАЧАЛО ИЗМЕНЕНИЙ: Обновленный компонент PostPage
// ============================================
function PostPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  
  // ТУТ ДОБАВИЛ: новые состояния для редактирования комментариев
  const [editingComment, setEditingComment] = useState(null);
  const [editText, setEditText] = useState('');

  useEffect(() => {
    loadPost();
    loadComments();
  }, [id]);

  const loadPost = async () => {
    try {
      const response = await fetch(`${API_URL}/posts/${id}`);
      if (response.ok) {
        const data = await response.json();
        setPost(data);
      }
    } catch (error) {
      console.error('Ошибка загрузки поста:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadComments = async () => {
    try {
      const response = await fetch(`${API_URL}/posts/${id}/comments`);
      if (response.ok) {
        const data = await response.json();
        setComments(data || []);
      }
    } catch (error) {
      console.error('Ошибка загрузки комментариев:', error);
      setComments([]);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    try {
      const response = await fetch(`${API_URL}/posts/${id}/comments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text: newComment, postId: parseInt(id) })
      });

      if (response.ok) {
        setNewComment('');
        loadComments();
        loadPost(); // Обновляем счётчик комментариев
      }
    } catch (error) {
      console.error('Ошибка добавления комментария:', error);
    }
  };

  // ТУТ ДОБАВИЛ: функция для начала редактирования комментария
  const handleEditComment = (comment) => {
    alert('Кнопка нажата! Комментарий: ' + comment.text);
    setEditingComment(comment);
    setEditText(comment.text);
  };

  // ТУТ ДОБАВИЛ: функция для сохранения отредактированного комментария
  const handleSaveEdit = async () => {
    if (!editText.trim()) return;
    
    try {
      const response = await fetch(`${API_URL}/posts/${id}/comments/${editingComment.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          id: editingComment.id,
          text: editText,
          postId: parseInt(id)
        })
      });
      
      if (response.ok) {
        setEditingComment(null);
        setEditText('');
        loadComments(); // Перезагрузить комментарии
      }
    } catch (error) {
      console.error('Ошибка редактирования комментария:', error);
    }
  };

  // ТУТ ДОБАВИЛ: функция для отмены редактирования
  const handleCancelEdit = () => {
    setEditingComment(null);
    setEditText('');
  };

  // ТУТ ДОБАВИЛ: функция для удаления комментария
  const handleDeleteComment = async (commentId) => {

    try {
      const response = await fetch(`${API_URL}/posts/${id}/comments/${commentId}`, {
        method: 'DELETE'
      });
      
      if (response.ok) {
        loadComments(); // Перезагрузить комментарии
        loadPost(); // Обновить счётчик комментариев
      }
    } catch (error) {
      console.error('Ошибка удаления комментария:', error);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Удалить пост?')) return;

    try {
      const response = await fetch(`${API_URL}/posts/${id}`, { method: 'DELETE' });
      if (response.ok) {
        navigate('/');
      }
    } catch (error) {
      console.error('Ошибка удаления поста:', error);
    }
  };

  if (loading) return <div className="loading">Загрузка...</div>;
  if (!post) return <div className="error">Пост не найден</div>;

  return (
    <div className="container">
      <header className="header">
        <Link to="/" className="btn btn-secondary">← Назад</Link>
        <div>
          <button onClick={() => setEditing(true)} className="btn btn-primary">Редактировать</button>
          <button onClick={handleDelete} className="btn btn-danger">Удалить</button>
        </div>
      </header>

      {editing ? (
        <EditPostForm post={post} onSave={() => { loadPost(); setEditing(false); }} onCancel={() => setEditing(false)} />
      ) : (
        <>
          <article className="post-detail">
            <img
              src={`${API_URL}/posts/${id}/image`}
              alt={post.title}
              className="post-detail-image"
              onError={(e) => e.target.style.display = 'none'}
            />
            <h1>{post.title}</h1>
            <div className="post-tags">
              {post.tags?.map((tag, i) => (
                <span key={i} className="tag">#{tag}</span>
              ))}
            </div>
            <p className="post-detail-text">{post.text}</p>
            <div className="post-stats">
              <span>❤️ {post.likesCount || 0} лайков</span>
              <span>💬 {post.commentsCount || 0} комментариев</span>
            </div>
          </article>

          <section className="comments-section">
            <h2>Комментарии</h2>
            
            {/* ТУТ ДОБАВИЛ: форма редактирования комментария */}
            {editingComment && (
              <div className="edit-comment-form" style={{ 
                marginBottom: '20px', 
                padding: '15px', 
                background: '#f0f0f0', 
                borderRadius: '8px' 
              }}>
                <h3>Редактировать комментарий</h3>
                <textarea
                  value={editText}
                  onChange={(e) => setEditText(e.target.value)}
                  rows="3"
                  className="textarea"
                  style={{ marginBottom: '10px' }}
                />
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button onClick={handleSaveEdit} className="btn btn-primary">Сохранить</button>
                  <button onClick={handleCancelEdit} className="btn btn-secondary">Отмена</button>
                </div>
              </div>
            )}

            <form onSubmit={handleAddComment} className="comment-form">
              <textarea
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Написать комментарий..."
                rows="3"
                className="textarea"
              />
              <button type="submit" className="btn btn-primary">Отправить</button>
            </form>

            <div className="comments-list">
              {comments.map(comment => (
                <div key={comment.id} className="comment">
                  <p>{comment.text}</p>
                  {/* ТУТ ДОБАВИЛ: кнопки редактирования и удаления для каждого комментария */}
                  <div style={{ display: 'flex', gap: '10px', marginTop: '5px' }}>
                    <button 
                      onClick={() => handleEditComment(comment)} 
                      className="btn-icon"
                      title="Редактировать"
                    >
                      ✏️
                    </button>
                    <button 
                      onClick={() => handleDeleteComment(comment.id)} 
                      className="btn-icon"
                      title="Удалить"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </>
      )}
    </div>
  );
}
// ============================================
// КОНЕЦ ИЗМЕНЕНИЙ
// ============================================

// Форма создания поста
function CreatePostPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    text: '',
    tags: ''
  });
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Создаём пост
      const response = await fetch(`${API_URL}/posts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: formData.title,
          text: formData.text,
          tags: formData.tags.split(',').map(t => t.trim()).filter(t => t)
        })
      });

      if (response.ok) {
        const post = await response.json();

        // Загружаем изображение, если есть
        if (image) {
          const formData = new FormData();
          formData.append('image', image);
          await fetch(`${API_URL}/posts/${post.id}/image`, {
            method: 'PUT',
            body: formData
          });
        }

        navigate(`/posts/${post.id}`);
      }
    } catch (error) {
      console.error('Ошибка создания поста:', error);
      alert('Ошибка при создании поста');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <header className="header">
        <Link to="/" className="btn btn-secondary">← Назад</Link>
        <h1>Создать пост</h1>
      </header>

      <form onSubmit={handleSubmit} className="post-form">
        <div className="form-group">
          <label>Заголовок</label>
          <input
            type="text"
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            required
            className="input"
          />
        </div>

        <div className="form-group">
          <label>Текст</label>
          <textarea
            value={formData.text}
            onChange={(e) => setFormData({ ...formData, text: e.target.value })}
            required
            rows="10"
            className="textarea"
          />
        </div>

        <div className="form-group">
          <label>Теги (через запятую)</label>
          <input
            type="text"
            value={formData.tags}
            onChange={(e) => setFormData({ ...formData, tags: e.target.value })}
            placeholder="javascript, react, web"
            className="input"
          />
        </div>

        <div className="form-group">
          <label>Изображение</label>
          <input
            type="file"
            accept="image/*"
            onChange={(e) => setImage(e.target.files[0])}
            className="input"
          />
        </div>

        <button type="submit" disabled={loading} className="btn btn-primary btn-large">
          {loading ? 'Создание...' : 'Создать пост'}
        </button>
      </form>
    </div>
  );
}

// Форма редактирования поста
function EditPostForm({ post, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    title: post.title,
    text: post.text,
    tags: post.tags?.join(', ') || ''
  });
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Обновляем текст поста
      const response = await fetch(`${API_URL}/posts/${post.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: formData.title,
          text: formData.text,
          tags: formData.tags.split(',').map(t => t.trim()).filter(t => t)
        })
      });

      if (response.ok) {
        // Загружаем новое изображение, если выбрано
        if (image) {
          const fd = new FormData();
          fd.append('image', image);
          await fetch(`${API_URL}/posts/${post.id}/image`, {
            method: 'PUT',
            body: fd
          });
        }

        alert('Пост обновлён!');
        onSave();
      }
    } catch (error) {
      console.error('Ошибка обновления поста:', error);
      alert('Ошибка при обновлении поста');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="post-form">
      <div className="form-group">
        <label>Заголовок</label>
        <input
          type="text"
          value={formData.title}
          onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          required
          className="input"
        />
      </div>

      <div className="form-group">
        <label>Текст</label>
        <textarea
          value={formData.text}
          onChange={(e) => setFormData({ ...formData, text: e.target.value })}
          required
          rows="10"
          className="textarea"
        />
      </div>

      <div className="form-group">
        <label>Теги (через запятую)</label>
        <input
          type="text"
          value={formData.tags}
          onChange={(e) => setFormData({ ...formData, tags: e.target.value })}
          className="input"
        />
      </div>

      <div className="form-group">
        <label>Новое изображение (необязательно)</label>
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setImage(e.target.files[0])}
          className="input"
        />
      </div>

      <div className="form-actions">
        <button type="submit" disabled={loading} className="btn btn-primary">
          {loading ? 'Сохранение...' : 'Сохранить'}
        </button>
        <button type="button" onClick={onCancel} className="btn btn-secondary">
          Отмена
        </button>
      </div>
    </form>
  );
}

// Главное приложение с роутингом
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/posts/:id" element={<PostPage />} />
        <Route path="/create" element={<CreatePostPage />} />
      </Routes>
    </Router>
  );
}

export default App;