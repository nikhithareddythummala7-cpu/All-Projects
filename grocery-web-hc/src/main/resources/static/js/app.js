const API_BASE = "/api";

function getAuth(){
	try{ return JSON.parse(localStorage.getItem("auth")||"null"); }catch{ return null; }
}
// --------- Order details modal helpers ---------
function adminOpenOrderDetailsFromObj(o){
  // Render body
  const body = document.getElementById('mo-body');
  const statusSel = document.getElementById('mo-status');
  const idInput = document.getElementById('mo-id');
  if(!body || !statusSel || !idInput) return;
  idInput.value = o.id;
  body.innerHTML = `
    <div><strong>Order</strong> ${o.id} — <em>${o.status}</em></div>
    <div>${(o.orderItems||[]).map(oi => `${oi.name||'Item'} x${oi.quantity} — ${formatINR(oi.price||0)} (${formatINR(oi.subtotal||0)})`).join('<br>')}</div>
    <div><small>Total: ${formatINR(o.totalAmount||0)}</small></div>
    <div><small>Ship to: ${o.shippingAddress||'-'} • Phone: ${o.phoneNumber||'-'}</small></div>
  `;
  statusSel.innerHTML = ['PENDING','SHIPPED','DELIVERED','CANCELLED']
    .map(s => `<option value="${s}" ${String(o.status)===s?'selected':''}>${s}</option>`).join('');
  const err = document.getElementById('modal-order-error');
  if(err) err.textContent = '';
  openModal('modal-order');
}

function adminOpenOrderDetails(o){
  // if o is an id string, fetch; if object, render directly
  if(typeof o === 'string'){
    // optional: fetch by id if an admin endpoint existed. For now, no-op.
    alert('Order fetch by id not implemented');
  } else {
    adminOpenOrderDetailsFromObj(o);
  }
}

async function adminSaveOrderStatus(){
  const orderId = document.getElementById('mo-id')?.value;
  const status = document.getElementById('mo-status')?.value;
  if(!orderId || !status) return;
  try{
    const res = await apiFetch(`${API_BASE}/orders/admin/${orderId}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status })
    });
    if(!res.ok){ let m='Failed to update status'; try{ const d=await res.json(); m=d.error||d.message||m; }catch{} throw new Error(m); }
    closeModal('modal-order');
    const sel = document.getElementById('admin-order-filter');
    adminLoadOrders(sel ? sel.value : 'ALL');
  }catch(e){ const err = document.getElementById('modal-order-error'); if(err) err.textContent = e.message; else alert(e.message); }
}

// --------- Modal helpers ---------
function openModal(id){ const el = document.getElementById(id); if(el){ el.style.display = 'flex'; } }
function closeModal(id){ const el = document.getElementById(id); if(el){ el.style.display = 'none'; } }

// Currency formatting (INR)
function formatINR(value){
  try{
    const n = Number(value||0);
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(n);
  }catch{
    return `₹${Number(value||0).toFixed(2)}`;
  }
}

function setAuth(data){
	if(!data){ localStorage.removeItem("auth"); return; }
	localStorage.setItem("auth", JSON.stringify(data));
}

function authHeaders(){
	const auth = getAuth();
	return auth?.token ? { "Authorization": `Bearer ${auth.token}` } : {};
}

async function apiFetch(url, options={}){
	const opts = { ...options };
	opts.headers = { ...(options.headers||{}), ...authHeaders() };
	// Auto-set JSON content-type if sending a body and header not provided
	if (opts.body && !opts.headers["Content-Type"]) {
		opts.headers["Content-Type"] = "application/json";
	}
	const res = await fetch(url, opts);
	if (res.status === 401) {
		setAuth(null);
		// Redirect to login for any unauthorized API call
		window.location.href = "/login";
		throw new Error("Unauthorized. Redirecting to login...");
	}
	return res;
}

function updateAuthLink(){
    const link = document.getElementById("auth-link");
    const adminLink = document.getElementById("admin-link");
    if(!link) return;
    const auth = getAuth();
    if(auth){
        link.textContent = `Logout (${auth.username})`;
        link.href = "#";
        link.onclick = (e)=>{ e.preventDefault(); setAuth(null); window.location.href = "/"; };
        if (adminLink) {
            const isAdmin = String(auth.role||'').toUpperCase() === 'ADMIN';
            adminLink.style.display = isAdmin ? '' : 'none';
        }
    }else{
        link.textContent = "Login";
        link.href = "/login";
        link.onclick = null;
        if (adminLink) adminLink.style.display = 'none';
    }
}

// Auth
async function handleLoginSubmit(e){
	e.preventDefault();
	const username = document.getElementById("login-username").value.trim();
	const password = document.getElementById("login-password").value;
	const out = document.getElementById("login-error");
	out.textContent = "";
	try{
		const res = await fetch(`${API_BASE}/auth/signin`, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ username, password })
		});
		const data = await res.json();
		if(!res.ok){ throw new Error(data.error || "Login failed"); }
		const token = data.accessToken || data.token;
		if(!token) throw new Error("No token received");
		setAuth({ token, userId: data.id, username: data.username, role: data.role });
		const isAdmin = String(data.role||'').toUpperCase() === 'ADMIN';
		window.location.href = isAdmin ? "/admin" : "/products";
	}catch(err){ out.textContent = err.message; }
}

async function handleSignupSubmit(e){
	e.preventDefault();
	const username = document.getElementById("signup-username").value.trim();
	const email = document.getElementById("signup-email").value.trim();
	const role = (document.getElementById("signup-role")?.value || 'USER').toUpperCase();
	const password = document.getElementById("signup-password").value;
	const out = document.getElementById("signup-error");
	out.textContent = "";
	try{
		const res = await fetch(`${API_BASE}/auth/signup`, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ username, email, password, role })
		});
		const data = await res.json();
		if(!res.ok){ throw new Error(data.error || "Signup failed"); }
		const token = data.accessToken || data.token;
		if(!token) throw new Error("No token received");
		setAuth({ token, userId: data.id, username: data.username, role: data.role });
		const isAdmin = String(data.role||'').toUpperCase() === 'ADMIN';
		window.location.href = isAdmin ? "/admin" : "/products";
	}catch(err){ out.textContent = err.message; }
}

// Products
async function loadProducts(query){
	const container = document.getElementById("products");
	const errorBox = document.getElementById("products-error");
	if(!container) return;
	errorBox.textContent = "";
	container.innerHTML = "Loading...";
	try{
		const url = query && query.length>0 ? `${API_BASE}/products/search?name=${encodeURIComponent(query)}` : `${API_BASE}/products`;
		const res = await fetch(url);
		if(!res.ok) throw new Error("Failed to load products");
		const products = await res.json();
		const auth = getAuth();
		const placeholder = `data:image/svg+xml;utf8,${encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300"><rect width="100%" height="100%" fill="#1f2937"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#9ca3af" font-family="Arial, sans-serif" font-size="20">No Image</text></svg>')}`;
		function resolveImageUrl(raw){
			if(!raw || typeof raw !== 'string') return placeholder;
			let u = raw.trim();
			// strip accidental wrapping quotes from DB values
			if ((u.startsWith('"') && u.endsWith('"')) || (u.startsWith("'") && u.endsWith("'"))) {
				u = u.slice(1, -1).trim();
			}
			if(u.length === 0) return placeholder;
			// Extract embedded http(s) URL if present inside noisy string
			const m = u.match(/https?:\/\/[^\s"']+/i);
			if (m && m[0]) u = m[0];
			// if host-only without protocol, assume https
			if(u.startsWith('images.')) u = `https://${u}`;
			if(u.startsWith('http://') || u.startsWith('https://') || u.startsWith('data:')) return `/proxy/img?src=${encodeURIComponent(u)}`;
			if(u.startsWith('/')) return u; // already absolute to host, e.g. /images/foo.jpg
			return `/images/${u}`; // treat as file name under static images
		}
		        const isAdmin = String((getAuth()?.role)||'').toUpperCase() === 'ADMIN';
        container.innerHTML = products.map(p => `
            <div class="card">
                <img src="${resolveImageUrl(p.imageUrl)}" alt="${p.name}"
                    style="width:100%;border-radius:8px;aspect-ratio:4/3;object-fit:cover"
                    referrerpolicy="no-referrer" crossorigin="anonymous" loading="lazy"
                    onerror="this.onerror=null;this.src='${placeholder}'">
                <h3>${p.name}</h3>
                <div class="price">${formatINR(p.price)} / ${p.unit||"unit"}</div>
                ${isAdmin
                  ? `<div class="actions"><button class="button small" onclick="adminOpenProductEdit('${p.id}')">Update</button><button class="button small secondary" onclick="adminDeleteProduct('${p.id}')">Delete</button></div>`
                  : (auth ? `<button class=\"button\" onclick=\"addToCart('${p.id}')\">Add to cart</button>` : `<a class=\"button\" href=\"/login\">Login to buy</a>`) }
            </div>
        `).join("");
	}catch(err){ container.innerHTML = ""; errorBox.textContent = err.message; }
}

function handleSearchInput(e){ loadProducts(e.target.value.trim()); }

// Cart
async function addToCart(productId){
	const auth = getAuth();
	if(!auth){ window.location.href = "/login"; return; }
	try{
		const res = await apiFetch(`${API_BASE}/cart`, {
			method: "POST",
			body: JSON.stringify({ userId: auth.userId, productId, quantity: 1 })
		});
		if(!res.ok){
			let msg = "Failed to add to cart";
			try{ const data = await res.json(); if(data && (data.error || data.message)) msg = data.error || data.message; }catch{}
			throw new Error(msg);
		}
		alert("Added to cart");
	}catch(err){ alert(err.message); }
}

async function loadCart(){
	const auth = getAuth();
	const list = document.getElementById("cart-items");
	const total = document.getElementById("cart-total");
	const errorBox = document.getElementById("cart-error");
	if(!list) return;
	if(!auth){ window.location.href = "/login"; return; }
	errorBox.textContent = "";
	list.innerHTML = "Loading...";
	try{
		const res = await apiFetch(`${API_BASE}/cart/${auth.userId}`);
		if(!res.ok) throw new Error("Failed to load cart");
		const items = await res.json();
		list.innerHTML = items.map(i => `
			<div class="item">
				<div>
					<div>${i.product?.name||"Item"}</div>
					<small>${formatINR(i.product?.price)}</small>
				</div>
				<div>
					<input type="number" min="0" value="${i.quantity}" style="width:70px" onchange="updateCartItem('${i.id}', this.value)">
					<button class="button secondary" onclick="removeCartItem('${i.id}')">Remove</button>
				</div>
			</div>
		`).join("");
		const totalRes = await apiFetch(`${API_BASE}/cart/total/${auth.userId}`);
		const totalVal = await totalRes.json();
		total.textContent = `Total: ${formatINR(totalVal)}`;
	}catch(err){ list.innerHTML = ""; errorBox.textContent = err.message; }
}

async function updateCartItem(itemId, qty){
	const q = Math.max(0, parseInt(qty||0,10));
	const res = await apiFetch(`${API_BASE}/cart/${itemId}?quantity=${q}`, { method: "PUT" });
	if(res.ok){ loadCart(); }
}

async function removeCartItem(itemId){
	const res = await apiFetch(`${API_BASE}/cart/${itemId}`, { method: "DELETE" });
	if(res.ok){ loadCart(); }
}
function handleCheckout(){
  const auth = getAuth();
  if(!auth){ window.location.href = "/login"; return; }
  window.location.href = "/checkout";
}

// Orders
async function submitCheckout(e){
  e.preventDefault();
  const auth = getAuth();
  if(!auth){ window.location.href = "/login"; return; }
  const errorBox = document.getElementById("checkout-error");
  if(errorBox) errorBox.textContent = "";
  try{
    const address = document.getElementById('address')?.value?.trim() || "";
    const phone = document.getElementById('phone')?.value?.trim() || "";
    const payment = document.getElementById('payment')?.value || "COD";

    if(address.length === 0) throw new Error("Address is required");
    if(phone.length === 0) throw new Error("Phone number is required");

    const cartRes = await apiFetch(`${API_BASE}/cart/${auth.userId}`);
    if(!cartRes.ok) throw new Error("Failed to load cart");
    const items = await cartRes.json();
    const orderItems = items.map(i => ({ productId: i.product?.id || i.productId, quantity: i.quantity }));
    if(orderItems.length === 0) throw new Error("Your cart is empty");

    const res = await apiFetch(`${API_BASE}/orders`, {
      method: "POST",
      body: JSON.stringify({ orderItems, shippingAddress: address, paymentMethod: payment, phoneNumber: phone })
    });
    const data = await res.json().catch(()=>({}));
    if(!res.ok) throw new Error(data?.error || data?.message || "Checkout failed");

    alert("Order placed successfully!");
    window.location.href = "/orders";
  }catch(err){
    if(errorBox) errorBox.textContent = err.message;
    else alert(err.message);
  }
}

async function loadOrders(){
	const auth = getAuth();
	const list = document.getElementById("orders");
	const errorBox = document.getElementById("orders-error");
	if(!list) return;
	if(!auth){ window.location.href = "/login"; return; }
	errorBox.textContent = "";
	list.innerHTML = "Loading...";
	try{
		const res = await apiFetch(`${API_BASE}/orders`);
		if(!res.ok) throw new Error("Failed to load orders");
		const orders = await res.json();
		        list.innerHTML = orders.map(o => `
            <div class="order">
                <div><strong>Order</strong> ${o.id} — <em>${o.status}</em></div>
                <div>${(o.orderItems||[]).map(oi => `${oi.name||'Item'} x${oi.quantity}`).join(", ")}</div>
                <div><small>Total: ${formatINR(o.totalAmount||0)}</small></div>
                <div><small>Ship to: ${o.shippingAddress||'-'} • Phone: ${o.phoneNumber||'-'}</small></div>
            </div>
        `).join("");
	}catch(err){ list.innerHTML = ""; errorBox.textContent = err.message; }
}

// ----------------- ADMIN FUNCTIONS -----------------
async function adminLoadProducts(){
  const tbody = document.getElementById('admin-products');
  const err = document.getElementById('admin-products-error');
  if(!tbody) return;
  err && (err.textContent = '');
  tbody.innerHTML = '<tr><td colspan="4">Loading...</td></tr>';
  try{
    const res = await apiFetch(`${API_BASE}/products`);
    if(!res.ok) throw new Error('Failed to load products');
    const prods = await res.json();
    tbody.innerHTML = prods.map(p => `
      <tr>
        <td>${p.name}</td>
        <td>${formatINR(p.price)}</td>
        <td>${p.stock ?? 0}</td>
        <td class="actions">
          <button class="button small" onclick="adminOpenProductEdit('${p.id}')">Edit</button>
          <button class="button small secondary" onclick="adminDeleteProduct('${p.id}')">Delete</button>
        </td>
      </tr>
    `).join('');
  }catch(e){ tbody.innerHTML = ''; err && (err.textContent = e.message); }
}

async function adminCreateProduct(){
  const name = document.getElementById('p-name')?.value?.trim();
  const price = parseFloat(document.getElementById('p-price')?.value || '0');
  const unit = document.getElementById('p-unit')?.value?.trim();
  const category = document.getElementById('p-category')?.value?.trim();
  const imageUrl = document.getElementById('p-image')?.value?.trim();
  const stock = parseInt(document.getElementById('p-stock')?.value || '0', 10);
  const inStock = !!document.getElementById('p-instock')?.checked;
  const err = document.getElementById('admin-products-error');
  err && (err.textContent = '');
  try{
    const res = await apiFetch(`${API_BASE}/products`, {
      method: 'POST',
      body: JSON.stringify({ name, price, unit, category, imageUrl, stock, inStock })
    });
    if(!res.ok){
      let msg = 'Failed to create product';
      try{ const data = await res.json(); msg = data.error || data.message || msg; }catch{}
      throw new Error(msg);
    }
    // clear inputs
    ['p-name','p-price','p-unit','p-category','p-image','p-stock'].forEach(id => { const el = document.getElementById(id); if(el) el.value=''; });
    document.getElementById('p-instock').checked = true;
    await adminLoadProducts();
  }catch(e){ err && (err.textContent = e.message); }
}

async function adminDeleteProduct(id){
  if(!confirm('Delete this product?')) return;
  const err = document.getElementById('admin-products-error');
  err && (err.textContent = '');
  const res = await apiFetch(`${API_BASE}/products/${id}`, { method: 'DELETE' });
  if(res.ok){ adminLoadProducts(); }
  else{
    try{ const data = await res.json();
         const statusText = res.status === 404 ? 'Product not found (maybe already deleted).' : 'Delete failed';
         throw new Error(data.error || data.message || statusText);
    }
    catch(e){ err && (err.textContent = e.message || 'Delete failed'); }
  }
}

async function adminOpenProductEdit(id){
  const err = document.getElementById('modal-product-error');
  if(err) err.textContent = '';
  const res = await apiFetch(`${API_BASE}/products/${id}`);
  if(!res.ok){ alert('Failed to load product'); return; }
  const p = await res.json();
  document.getElementById('mp-id').value = p.id;
  document.getElementById('mp-name').value = p.name || '';
  document.getElementById('mp-price').value = p.price || 0;
  document.getElementById('mp-unit').value = p.unit || '';
  document.getElementById('mp-category').value = p.category || '';
  document.getElementById('mp-image').value = p.imageUrl || '';
  document.getElementById('mp-stock').value = p.stock ?? 0;
  document.getElementById('mp-instock').checked = !!p.inStock;
  openModal('modal-product');
}

async function adminSaveProductEdit(){
  const id = document.getElementById('mp-id').value;
  const name = document.getElementById('mp-name').value.trim();
  const price = parseFloat(document.getElementById('mp-price').value || '0');
  const unit = document.getElementById('mp-unit').value.trim();
  const category = document.getElementById('mp-category').value.trim();
  const imageUrl = document.getElementById('mp-image').value.trim();
  const stock = parseInt(document.getElementById('mp-stock').value || '0', 10);
  const inStock = !!document.getElementById('mp-instock').checked;
  const err = document.getElementById('modal-product-error');
  if(err) err.textContent = '';
  try{
    const payload = { name, price, unit, category, imageUrl, stock, inStock };
    let updRes;
    if(id){
      updRes = await apiFetch(`${API_BASE}/products/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
    } else {
      updRes = await apiFetch(`${API_BASE}/products`, { method: 'POST', body: JSON.stringify(payload) });
    }
    if(!updRes.ok){ let m='Save failed'; try{ const d=await updRes.json(); m=d.error||d.message||m; }catch{} throw new Error(m); }
    closeModal('modal-product');
    // If on admin page, reload list; if on products page, refresh products
    if(document.getElementById('admin-products')) await adminLoadProducts();
    if(document.getElementById('products')) await loadProducts(document.getElementById('search')?.value||'');
  }catch(e){ if(err) err.textContent = e.message; else alert(e.message); }
}

function adminOpenProductCreate(){
  const err = document.getElementById('modal-product-error');
  if(err) err.textContent = '';
  document.getElementById('mp-id').value = '';
  document.getElementById('mp-name').value = '';
  document.getElementById('mp-price').value = '';
  document.getElementById('mp-unit').value = '';
  document.getElementById('mp-category').value = '';
  document.getElementById('mp-image').value = '';
  document.getElementById('mp-stock').value = '';
  document.getElementById('mp-instock').checked = true;
  openModal('modal-product');
}

async function adminLoadUsers(){
  const tbody = document.getElementById('admin-users');
  const err = document.getElementById('admin-users-error');
  if(!tbody) return;
  err && (err.textContent = '');
  tbody.innerHTML = '<tr><td colspan="4">Loading...</td></tr>';
  try{
    const res = await apiFetch(`${API_BASE}/admin/users`);
    if(!res.ok) throw new Error('Failed to load users');
    const users = (await res.json()).filter(u => String(u.role||'').toUpperCase() === 'USER');
    tbody.innerHTML = users.map(u => `
      <tr>
        <td>${u.username}</td>
        <td>${u.email}</td>
        <td>${u.role}</td>
        <td><button class="button small" onclick="adminShowUserOrders('${u.id}', '${u.username}')">View orders</button></td>
      </tr>
    `).join('');
  }catch(e){ tbody.innerHTML=''; err && (err.textContent = e.message); }
}

async function adminShowUserOrders(userId, username){
  try{
    const res = await apiFetch(`${API_BASE}/admin/users/${userId}/orders`);
    if(!res.ok) throw new Error('Failed to load user orders');
    const orders = await res.json();
    alert(`${username}'s orders:\n\n` + orders.map(o => `#${o.id} - ${o.status}`).join('\n'));
  }catch(e){ alert(e.message); }
}

async function adminLoadOrders(filter){
  const wrap = document.getElementById('admin-orders');
  const err = document.getElementById('admin-orders-error');
  if(!wrap) return;
  err && (err.textContent = '');
  wrap.innerHTML = 'Loading...';
  try{
    let res;
    if(filter && filter !== 'ALL') res = await apiFetch(`${API_BASE}/orders/admin/status/${filter}`);
    else res = await apiFetch(`${API_BASE}/orders/admin`);
    if(!res.ok) throw new Error('Failed to load orders');
    const orders = await res.json();
    wrap.innerHTML = orders.map(o => `
      <div class="order">
        <div><strong>Order</strong> ${o.id} — <em>${o.status}</em></div>
        <div>${(o.orderItems||[]).map(oi => `${oi.product?.name||oi.name||'Item'} x${oi.quantity}`).join(', ')}</div>
        <div><small>Total: ${formatINR(o.totalAmount||0)}</small></div>
        <div class="row" style="gap:6px; align-items:center;">
          <label>Status:</label>
          <select onchange="adminUpdateOrderStatus('${o.id}', this.value)">
            ${['PENDING','SHIPPED','DELIVERED','CANCELLED'].map(s => `<option value="${s}" ${String(o.status)===s?'selected':''}>${s}</option>`).join('')}
          </select>
          <button class="button small" onclick='adminOpenOrderDetails(${JSON.stringify({
            id: ''
          })})'>Details</button>
          
        </div>
      </div>
    `).join('');
  }catch(e){ wrap.innerHTML=''; err && (err.textContent = e.message); }
}

async function adminUpdateOrderStatus(orderId, status){
  try{
    const res = await apiFetch(`${API_BASE}/orders/admin/${orderId}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status })
    });
    if(!res.ok){ let m='Failed to update status'; try{ const d=await res.json(); m=d.error||d.message||m; }catch{} throw new Error(m); }
    // reload section
    const sel = document.getElementById('admin-order-filter');
    adminLoadOrders(sel ? sel.value : 'ALL');
    alert('Order status updated');
  }catch(e){ alert(e.message); }
}

// expose admin functions
window.adminLoadProducts = adminLoadProducts;
window.adminCreateProduct = adminCreateProduct;
window.adminDeleteProduct = adminDeleteProduct;
window.adminOpenProductEdit = adminOpenProductEdit;
window.adminSaveProductEdit = adminSaveProductEdit;
window.adminOpenProductCreate = adminOpenProductCreate;
window.adminLoadUsers = adminLoadUsers;
window.adminShowUserOrders = adminShowUserOrders;
window.adminLoadOrders = adminLoadOrders;
window.adminUpdateOrderStatus = adminUpdateOrderStatus;
window.adminOpenOrderDetails = adminOpenOrderDetails;
window.adminSaveOrderStatus = adminSaveOrderStatus;
window.openModal = openModal;
window.closeModal = closeModal;

// --- Admin products cards (dashboard) ---
async function adminLoadProductsGrid(query){
  const grid = document.getElementById('admin-products-grid');
  if(!grid) return;
  grid.innerHTML = 'Loading...';
  try{
    // helper to normalize image URL similar to loadProducts()
    function resolveImageUrl(raw){
      const placeholder = `data:image/svg+xml;utf8,${encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300"><rect width="100%" height="100%" fill="#1f2937"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#9ca3af" font-family="Arial, sans-serif" font-size="20">No Image</text></svg>')}`;
      if(!raw || typeof raw !== 'string') return placeholder;
      let u = raw.trim();
      if ((u.startsWith('"') && u.endsWith('"')) || (u.startsWith("'") && u.endsWith("'"))) { u = u.slice(1, -1).trim(); }
      if(u.length === 0) return placeholder;
      const m = u.match(/https?:\/\/[^\s"']+/i); if(m && m[0]) u = m[0];
      if(u.startsWith('images.')) u = `https://${u}`;
      if(u.startsWith('http://') || u.startsWith('https://') || u.startsWith('data:')) return `/proxy/img?src=${encodeURIComponent(u)}`;
      if(u.startsWith('/')) return u;
      return `/images/${u}`;
    }

    const url = query && query.trim().length>0 ? `${API_BASE}/products/search?name=${encodeURIComponent(query.trim())}` : `${API_BASE}/products`;
    const res = await apiFetch(url);
    if(!res.ok) throw new Error('Failed to load products');
    const prods = await res.json();
    grid.innerHTML = prods.map(p => `
      <div class="card">
        <img src="${resolveImageUrl(p.imageUrl)}" alt="${p.name}" style="width:100%;border-radius:8px;aspect-ratio:4/3;object-fit:cover"
             referrerpolicy="no-referrer" crossorigin="anonymous" loading="lazy">
        <h3>${p.name}</h3>
        <div class="price">${formatINR(p.price)} / ${p.unit||'unit'}</div>
        <div class="actions">
          <button class="button small" onclick="adminOpenProductEdit('${p.id}')">Update</button>
          <button class="button small secondary" onclick="adminDeleteProduct('${p.id}')">Delete</button>
        </div>
      </div>
    `).join('');
  }catch(e){ grid.innerHTML = `<div class="error">${e.message}</div>`; }
}

window.adminLoadProductsGrid = adminLoadProductsGrid;
