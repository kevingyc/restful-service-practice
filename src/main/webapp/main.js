window.onload = function() {
	var AUTH_TOKEN = window.localStorage.getItem("AUTH_TOKEN");

	if (AUTH_TOKEN === null || AUTH_TOKEN === undefined || AUTH_TOKEN === "undefined") {
		AUTH_TOKEN = "";
	}

	var axiosInstance = axios.create({
		baseURL: '/restful-services/services/v1/rest',
		timeout: 10000,
		headers: {
			'Authorization': AUTH_TOKEN
		}
	});

	const store = new Vuex.Store({
		state: {
			isLogin: false
		},
		getters: {
			getIsLogin: state => {
				return state.isLogin;
			}
		},
		actions: {
			setIsLogin(state, isLogin) {
				state.isLogin = isLogin;
			}
		},
		mutations: {
			login(state) {
				state.isLogin = true;
			},
			logout(state) {
				state.isLogin = false;
			}
		}
	});


	const loginSection = Vue.component('login-section', {
		template: '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" id="login" v-show="isLogin === false"><h1>Login</h1><form v-on:submit.prevent="login"><div class="form-group"><label for="inputName">Name</label><input type="text" class="form-control" id="inputName" v-model="name" placeholder="Name"></div><div class="form-group"><label for="inputPassword">Password</label><input type="password" class="form-control" id="inputPassword" v-model="pwd" placeholder="Password"></div><button type="submit" class="btn btn-default" v-on:click="checkLogin">Login</button><div class="label" v-bind:class="messageClass">{{ message }}</div></form></div>',
		data: function() {
			return {
				message: '',
				errorMessage: 'ERROR!',
				messageClass: '',
				isLogin: false,
				name: '',
				pwd: ''
			}
		},
		watch: {
			isLogin: function(val) {
				console.log("[login] login state change");
				console.log(val);
			}
		},
		created: function() {
			var self = this;

			var token = window.localStorage.getItem("AUTH_TOKEN");

			if (token === null || token === undefined || token === "undefined") {
				token = ""
			}

			axiosInstance.defaults.headers['Authorization'] = token;

			axiosInstance.get('/checkLogin')
				.then(function(response) {
					console.log("authorized")
					window.localStorage.setItem("AUTH_TOKEN", response.headers.authorization);
					self.isLogin = true;
					self.$store.commit("login");
					self.checkLogin();
				})
				.catch(function(error) {
					console.log("unauthorized")
					self.isLogin = false;
					self.$store.commit("logout");
					self.checkLogin();
				});

			self.$bus.$on('checkLogin', event => {
				self.isLogin = self.$store.state.isLogin;
			});
		},
		methods: {
			login: function() {
				var self = this;
				axiosInstance.post('/', {
						"name": self.name,
						"pwd": self.pwd
					})
					.then(function(response) {
						console.log("login");

						if (response.data.token === undefined) {
							self.messageClass = 'label-danger';
							self.message = response.data.replace(/_/g, " ");
						} else {
							window.localStorage.setItem("AUTH_TOKEN", response.data.token);

							self.isLogin = true;
							self.$store.commit("login");
							self.checkLogin();
						}

					})
					.catch(function(error) {
						console.log("login error");
						self.isLogin = false;
						self.$store.commit("logout");
						self.checkLogin();
					});
			},
			checkLogin: function() {
				this.$bus.$emit('checkLogin');
			}
		},
		beforeDestroy: function() {
			this.$bus.$off('checkLogin');
		}

	})


	const logoutSection = Vue.component('logout-section', {
		template: '<div id="logout" v-show="isLogin === true"><button type="submit" class="btn btn-default" v-on:click="logout">Log out</button></div>',
		data: function() {
			return {
				message: '',
				errorMessage: 'ERROR!',
				isLogin: false
			}
		},
		watch: {
			isLogin: function(val) {
				console.log("[logout] login state change");
				console.log(val);
			}
		},
		created: function() {
			var self = this;

			self.isLogin = self.$store.state.isLogin;

			self.$bus.$on('checkLogin', event => {
				self.isLogin = self.$store.state.isLogin;
			});
		},
		methods: {
			logout: function() {
				var self = this;
				window.localStorage.removeItem("AUTH_TOKEN");
				self.$store.commit("logout");
				self.checkLogin();
			},
			checkLogin: function() {
				this.$bus.$emit('checkLogin');
			}
		},
		beforeDestroy: function() {
			this.$bus.$off('checkLogin');
		}
	});


	const membersSection = Vue.component('members-section', {
		template: '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 table-responsive panel panel-primary" id="members" v-show="isLogin === true"><div class="panel-heading">Get Members List</div><button v-on:click="getMembers" class="btn btn-default">Get Member List</button><table class="table"><thead><tr><th v-for="column in columns">{{column}}</th></tr></thead><tbody><tr v-for="member in members"><th scope="row">{{member.id}}</th><td>{{member.name}}</td></tr></tbody></table></div>',
		data: function() {
			return {
				message: 'Hello Vue table!',
				errorMessage: 'ERROR!',
				isLogin: false,
				columns: [
					"#", "Name"
				],
				members: []
			}
		},
		watch: {
			members: function(val) {
				console.log("member change");
				console.log(val.length);
			},
			isLogin: function(val) {
				var self = this;
				if (!val) {
					self.members = [];
				}
			}
		},
		created: function() {
			var self = this;

			self.isLogin = self.$store.state.isLogin;

			self.$bus.$on('checkLogin', event => {
				self.isLogin = self.$store.state.isLogin;
			});
		},
		methods: {
			getMembers: function() {
				var self = this;

				var token = window.localStorage.getItem("AUTH_TOKEN");

				if (token === null || token === undefined || token === "undefined") {
					token = "";
				}

				axiosInstance.defaults.headers['Authorization'] = token;

				axiosInstance.get('/member')
					.then(function(response) {
						window.localStorage.setItem("AUTH_TOKEN", response.headers.authorization);
						self.members = response.data.members;
						console.log(self.members.length);
					})
					.catch(function(error) {
						console.log("get member error")
						this.members = "no members";
					});
			},
			checkLogin: function() {
				this.$bus.$emit('checkLogin');
			}
		},
		beforeDestroy: function() {
			this.$bus.$off('checkLogin');
		}

	});

	const createMemberSection = Vue.component('create-member-section', {
		template: '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 panel panel-primary" id="create-member" v-show="isLogin === true"><div class="panel-heading">Create Member</div><form v-on:submit.prevent="createMember"><div class="form-group"><label for="inputName">Name</label><input type="text" class="form-control" id="inputName" v-model="name" placeholder="Name"></div><button type="submit" class="btn btn-default">Create Member</button><div class="label" v-bind:class="messageClass">{{ message }}</div></form></div>',
		data: function() {
			return {
				message: '',
				errorMessage: 'ERROR!',
				name: '',
				messageClass: '',
				isLogin: false
			}
		},
		watch: {
			isLogin: function(val) {
				var self = this;
				if (!val) {
					self.name = '';
					self.message = '';
					self.messageClass = '';
				}
			}
		},
		created: function() {
			var self = this;

			self.isLogin = self.$store.state.isLogin;

			self.$bus.$on('checkLogin', event => {
				self.isLogin = self.$store.state.isLogin;
			});
		},
		methods: {
			createMember: function() {
				var self = this;

				var token = window.localStorage.getItem("AUTH_TOKEN");

				if (token === null || token === undefined) {
					token = ""
				}

				if (self.name === "") {
					console.log("name null");
					self.messageClass = 'label-danger';
					self.message = "Need Name"
					return
				}

				axiosInstance.defaults.headers['Authorization'] = token;

				axiosInstance.post('/member', {
						"name": self.name
					})
					.then(function(response) {
						console.log("create member");
						window.localStorage.setItem("AUTH_TOKEN", response.headers.authorization);
						self.message = "success";
						self.messageClass = 'label-success';
					})
					.catch(function(error) {
						console.log("create member error");
						self.messageClass = 'label-danger';

						if (error.response.status === 409) {
							self.message = "Name Conflicted";
						} else {
							self.message = "failed";
						}
					});
			},
			checkLogin: function() {
				this.$bus.$emit('checkLogin');
			}
		},
		beforeDestroy: function() {
			this.$bus.$off('checkLogin');
		}
	});

	var bus = new Vue();

	Object.defineProperty(Vue.prototype, '$bus', {
		get() {
			return this.$root.bus;
		}
	});

	new Vue({
		el: '#app',
		store,
		data() {
			return {
				// Bind our event bus to our $root Vue model
				bus: bus
			}
		},
		components: {
			loginSection,
			logoutSection,
			membersSection,
			createMemberSection
		},
		template: '<div id="app"><login-section></login-section><logout-section></logout-section><members-section></members-section><create-member-section></create-member-section></div>'
	})

}