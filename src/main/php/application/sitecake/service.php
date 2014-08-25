<?php
namespace sitecake;

use \Exception as Exception;
use Zend\Json\Json as json;
use Zend\Http\Request as Request;

class service {
	
	static function execute($action, Request $req) {
		if ($action === null || empty($action) || 
				!method_exists('\sitecake\service', $action)) {
			return service::response($req->getQuery(),
				array('status' => -1, 'errorMessage' => resources::message(
					'INVALID_SERVICE_REQUEST', $_SERVER['REQUEST_URI'])));
		}
				
		if ($action == 'login' || $action == 'change' || service::auth()) {
			ob_start();
			try {
				$res = service::$action($req);
				meta::save();
			} catch (\Exception $e) {
				echo $e->getMessage() . "\n";
				echo $e->getTraceAsString();
			}
			$ob = ob_get_contents();
			ob_end_clean();
			return $ob ? service::response($req->getQuery(), 
				array('status' => -1, 'errorMessage' => $ob)) : $res;
		} else {
			return service::response($req->getQuery(), 
				array('status' => -1, 'errorMessage' => 'Not authorized'));
		}		
	}
	
	static function login(Request $req) {
		$params = $req->getQuery();
		return service::response($params, 
			session::login($params['credential']));
	}
	
	static function logout(Request $req) {
		return service::response($req->getQuery(), 
			session::logout());
	}
	
	static function change(Request $req) {
		$params = $req->getQuery();
		return service::response($params, 
			session::change($params['credential'], $params['newCredential']));
	}

	static function alive(Request $req) {
		return service::response($req->getQuery(),
			session::alive());
	}
	
	static function upload(Request $req) {
		return service::response($req->getQuery(), upload::upload_file($req));	
	}
	
	static function save(Request $req) {
		return service::response($req->getQuery(), content::save($req->getPost()));	
	}
	
	static function publish(Request $req) {
		return service::response($req->getQuery(), content::publish($req->getPost()));
	}
	
	static function upgrade(Request $req) {
		return service::response($req->getQuery(), upgrade::perform());	
	}
	
	static function pages(Request $req) {
		$params = $req->getPost();
		return service::response($req->getQuery(), 
			isset($params['pages']) ? pages::update(json::decode(
				stripcslashes($params['pages']), json::TYPE_ARRAY)) : pages::get());
	}
	
	static function image_transform(Request $req) {
		return service::response($req->getQuery(), image::transform($req->getPost()));	
	}
	
	private static function auth() {	
		session_start();
		return ($_SESSION['loggedin'] === true);
	}
	
	private static function response($params, $data)
	{
		$body = json::encode($data);
		return http::response(isset($params['callback']) ? 
				$params['callback'] . '(' . $body . ')' : $body);
	}
}