package jp.co.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.internous.ecsite.model.dao.GoodsRepository;
import jp.co.internous.ecsite.model.dao.UserRepository;
import jp.co.internous.ecsite.model.entity.Goods;
import jp.co.internous.ecsite.model.entity.User;
import jp.co.internous.ecsite.model.form.GoodsForm;
import jp.co.internous.ecsite.model.form.LoginForm;

@Controller
@RequestMapping("/ecsite/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepos;
	
	@Autowired
	private GoodsRepository goodsRepos;

		@RequestMapping("/")
		public String index() {
			return "adminindex";
		}
		
		//ユーザー情報を受け取る
		@PostMapping("/welcome")
		public String welcome(LoginForm form, Model m) {
			//ユーザー名とパスワードからユーザーを検索
			List<User> users=userRepos.findByUserNameAndPassword(form.getUserName(), form.getPassword());
			//ユーザーが存在していればisAdminで管理者かどうか判定し、管理者だった場合のみ実行
			if(users != null && users.size()>0) {
				boolean isAdmin=users.get(0).getIsAdmin() != 0;
				if(isAdmin) {
					List<Goods> goods = goodsRepos.findAll();
					m.addAttribute("userName",users.get(0).getUserName());
					m.addAttribute("password", users.get(0).getPassword());
					m.addAttribute("goods", goods);
				}
			}
			return "welcome";
		}
		
		//管理者ログイン
		@RequestMapping("/goodsMst")
		public String goodsMst(LoginForm form, Model m) {
			m.addAttribute("userName", form.getUserName());
			m.addAttribute("password", form.getPassword());
			
			return "goodsmst";
		}
		
		//管理者用新商品登録
		@RequestMapping("/addGoods")
		public String addGoods (GoodsForm goodsForm, LoginForm loginForm, Model m) {
			m.addAttribute("userName", loginForm.getUserName());
			m.addAttribute("password", loginForm.getPassword());
			
			Goods goods=new Goods();
			goods.setGoodsName(goodsForm.getGoodsName());
			goods.setPrice(goodsForm.getPrice());
			goodsRepos.saveAndFlush(goods);
			
			return "forward:/ecsite/admin/welcome";
		}
		
		//管理者用商品削除
		@ResponseBody
		@PostMapping("/api/deleteGoods")
		public String deleteApi(@RequestBody GoodsForm f, Model m) {
			try {
				goodsRepos.deleteById(f.getId());
			}catch (IllegalArgumentException e) {
				return "-1";
			}
			
			return "1";
		}
}
